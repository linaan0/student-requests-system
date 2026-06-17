package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.JoinedSubject;
import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.base.Semester;
import mk.ukim.finki.wp.molbi.model.base.TeacherSubjectAllocations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TeacherSubjectAllocationsRepository extends JpaSpecificationRepository<TeacherSubjectAllocations, Long> {

    List<TeacherSubjectAllocations> findBySubject(JoinedSubject joinedSubject, Sort sort);

    List<TeacherSubjectAllocations> findBySemesterCode(String semesterCode);

    List<TeacherSubjectAllocations> findTeacherSubjectAllocationsBySemester(Semester semester);

    Page<TeacherSubjectAllocations> findAll(Specification<TeacherSubjectAllocations> filter, Pageable page);

    @Modifying
    void deleteBySemesterCode(String semesterCode);

    @Query("select sum(tsa.numberOfLectureGroups) from TeacherSubjectAllocations tsa where tsa.subject.abbreviation = ?1 and tsa.semester.code=?2")
    Float getCoveredLectureGroupsInSemester(@Param("subject") String subject, @Param("semester") String semester);

    @Query("select sum(tsa.numberOfExerciseGroups) from TeacherSubjectAllocations tsa where tsa.subject.abbreviation = ?1 and tsa.semester.code=?2")
    Float getCoveredExerciseGroupsInSemester(@Param("subject") String subject, @Param("semester") String semester);

    @Query("select sum(tsa.numberOfLabGroups) from TeacherSubjectAllocations tsa where tsa.subject.abbreviation = ?1 and tsa.semester.code=?2")
    Float getCoveredLabGroupsInSemester(@Param("subject") String subject, @Param("semester") String semester);

    List<TeacherSubjectAllocations> findBySemesterCodeAndSubjectAbbreviation(String code, String abbreviation);

    List<TeacherSubjectAllocations> findBySemesterCodeAndSubjectAbbreviationOrderByProfessorOrderingRank(String code, String abbreviation);

    List<TeacherSubjectAllocations> findByProfessorId(String professorId);

    List<TeacherSubjectAllocations> findByProfessorIdAndSemesterCode(String professorId, String semesterCode);

    @Query("SELECT DISTINCT t.professor FROM TeacherSubjectAllocations t WHERE t.numberOfLectureGroups > 0 OR t.numberOfLabGroups > 0")
    List<Professor> findProfessorsWithAllocations();

    @Query("SELECT DISTINCT t.professor FROM TeacherSubjectAllocations t WHERE t.subject.abbreviation = :subjectAbbreviation AND (t.numberOfLectureGroups > 0 OR t.numberOfLabGroups > 0)")
    List<TeacherSubjectAllocations> findProfessorsBySubject(@Param("subjectAbbreviation") String subjectAbbreviation);

    @Query("SELECT DISTINCT p FROM TeacherSubjectAllocations t " +
            "JOIN Professor p ON t.professor.id = p.id " +
            "WHERE t.subject.abbreviation = :subjectAbbreviation " +
            "AND t.semesterCode = :semesterCode " +
            "AND (t.numberOfLectureGroups > 0 OR t.numberOfExerciseGroups > 0)")
    List<Professor> findProfessorsBySubjectAndSemester(@Param("subjectAbbreviation") String subjectAbbreviation,
                                                       @Param("semesterCode") String semesterCode);

}
