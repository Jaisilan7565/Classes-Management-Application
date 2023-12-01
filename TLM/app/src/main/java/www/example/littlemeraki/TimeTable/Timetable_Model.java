package www.example.littlemeraki.TimeTable;

public class Timetable_Model {

    public Timetable_Model() {
    }

    String course;

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Timetable_Model(String course, String mon_1, String mon_2, String tue_1, String tue_2, String wed_1, String wed_2, String thur_1, String thur_2, String fri_1, String fri_2, String sat_1, String sat_2) {
        this.course = course;
    }

}
