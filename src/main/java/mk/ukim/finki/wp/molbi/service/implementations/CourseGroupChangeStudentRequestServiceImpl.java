package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.event.RequestStatusChangedEvent;
import mk.ukim.finki.wp.molbi.model.base.*;
import mk.ukim.finki.wp.molbi.model.requests.CourseGroupChangeStudentRequest;
import mk.ukim.finki.wp.molbi.repository.*;
import mk.ukim.finki.wp.molbi.service.interfaces.CourseGroupChangeStudentRequestService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CourseGroupChangeStudentRequestServiceImpl
        extends StudentRequestServiceImpl<CourseGroupChangeStudentRequest>
        implements CourseGroupChangeStudentRequestService {

    private final JoinedSubjectRepository joinedSubjectRepository;
    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;
    private final StudentSubjectEnrollmentRepository enrollmentRepository;

    protected CourseGroupChangeStudentRequestServiceImpl(StudentRequestRepository<CourseGroupChangeStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, JoinedSubjectRepository joinedSubjectRepository, ProfessorRepository professorRepository, ApplicationEventPublisher eventPublisher, CourseRepository courseRepository, StudentSubjectEnrollmentRepository enrollmentRepository) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
        this.joinedSubjectRepository = joinedSubjectRepository;
        this.professorRepository = professorRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }


    @Override
    public CourseGroupChangeStudentRequest create(Long sessionId, String studentId,
                                                  String description, String joinedSubjectId,
                                                  String currentProfessorId, String newProfessorId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студентот не е пронајден."));
        JoinedSubject joinedSubject = joinedSubjectRepository.findById(joinedSubjectId)
                .orElseThrow(() -> new EntityNotFoundException("Предметот не е пронајден."));
        Professor currentProfessor = professorRepository.findById(currentProfessorId)
                .orElseThrow(() -> new EntityNotFoundException("Тековниот професор не е пронајден."));
        Professor newProfessor = professorRepository.findById(newProfessorId)
                .orElseThrow(() -> new EntityNotFoundException("Новиот професор не е пронајден"));

        if (currentProfessor.equals(newProfessor)) {
            throw new IllegalStateException(
                    "Новиот професор мора да биде различен од тековниот професор");
        }

        Semester semester = requestSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Сесијата не е пронајдена"))
                .getSemester();

        String enrollmentId = String.format("%s-%s-%s",
                semester.getCode(), student.getIndex(), joinedSubject.getMainSubject().getId());
        StudentSubjectEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Го немате запишано овој предмет."));

        if (enrollment.getCourse() == null ||
                !currentProfessor.equals(enrollment.getCourse().getProfessor())) {
            throw new IllegalStateException(
                    "Избраниот тековен професор не го предава избраниот предмет.");
        }

       courseRepository
                .findByJoinedSubjectAndProfessorAndSemester(joinedSubject, newProfessor, semester)
                .orElseThrow(() -> new IllegalStateException(
                        "Избраниот нов професор не го предава избраниот предмет."));

        CourseGroupChangeStudentRequest request = new CourseGroupChangeStudentRequest();
        request.setDescription(description);
        request.setJoinedSubject(joinedSubject);
        request.setCurrentProfessor(currentProfessor);
        request.setNewProfessor(newProfessor);
        populateBaseFields(request, sessionId, student);
        return repository.save(request);
    }

    @Override
    public boolean existsBySessionId(Long sessionId) {
        return repository.existsByRequestSession_Id(sessionId);
    }

    @Override
    public CourseGroupChangeStudentRequest approve(Long id) {
        CourseGroupChangeStudentRequest request = findById(id);
        if (!request.canBeApproved())
            throw new IllegalStateException("Молбата не може да биде одобрена");

        Student student = request.getStudent();
        Semester semester = request.getRequestSession().getSemester();
        Subject subject = request.getJoinedSubject().getMainSubject();

        String enrollmentId = String.format("%s-%s-%s",
                semester.getCode(), student.getIndex(), subject.getId());

        StudentSubjectEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Го немате запишано овој предмет."));

        if (enrollment.getCourse() == null ||
                !request.getCurrentProfessor().equals(enrollment.getCourse().getProfessor())) {
            throw new IllegalStateException(
                    "Избраниот тековен професор не го предава избраниот предмет.");
        }
        Course newCourse = courseRepository
                .findByJoinedSubjectAndProfessorAndSemester(
                        request.getJoinedSubject(), request.getNewProfessor(), semester)
                .orElseThrow(() -> new IllegalStateException(
                        "Избраниот нов професор не го предава избраниот предмет."));

        enrollment.setCourse(newCourse);

        enrollmentRepository.save(enrollment);

        request.setIsApproved(true);
        request.setIsProcessed(true);
        request.setDateProcessed(LocalDate.now());

        CourseGroupChangeStudentRequest saved = repository.save(request);
        eventPublisher.publishEvent(new RequestStatusChangedEvent(saved));
        return saved;
    }
}