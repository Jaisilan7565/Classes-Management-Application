package www.example.littlemeraki.Course_Division;

public class StudentInfo_Model {

    public StudentInfo_Model() {

    }

    public StudentInfo_Model(String name, String institute, String course, String fee, String contact, String joining_date, String due_date) {
        this.name = name;
        this.institute = institute;
        this.course = course;
        this.fee = fee;
        this.contact = contact;
        this.joining_date = joining_date;
        this.due_date = due_date;
    }

    public StudentInfo_Model(String name, String course, String due_date, String contact) {
        this.name = name;
        this.course = course;
        this.due_date=due_date;
        this.contact=contact;
    }

    String name;
    String institute;
    String course;
    String fee;
    String contact;
    String joining_date;
    String due_date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getJoining_date() {
        return joining_date;
    }

    public void setJoining_date(String joining_date) {
        this.joining_date = joining_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }


}
