package mk.ukim.finki.wp.molbi.model.enums;

public enum AppRole {
    PROFESSOR, ADMIN, STUDENT, GUEST, FINANCE_ADMIN;


    public String roleName() {
        return "ROLE_" + this.name();
    }
}
