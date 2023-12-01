package www.example.littlemeraki.Fee;

public class FeeModel {

    String course,fee;

    public FeeModel(){

    }

    public FeeModel(String course, String fee) {
        this.course = course;
        this.fee = fee;
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

}
