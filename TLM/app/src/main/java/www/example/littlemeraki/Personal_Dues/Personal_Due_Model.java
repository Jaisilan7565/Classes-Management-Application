package www.example.littlemeraki.Personal_Dues;

public class Personal_Due_Model {

    public Personal_Due_Model() {
    }

    String due_date;
    String due_fee;
    String paid_date;

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getDue_fee() {
        return due_fee;
    }

    public void setDue_fee(String due_fee) {
        this.due_fee = due_fee;
    }

    public String getPaid_date() {
        return paid_date;
    }

    public void setPaid_date(String paid_date) {
        this.paid_date = paid_date;
    }

    public Personal_Due_Model(String due_date, String due_fee, String paid_date) {
        this.due_date = due_date;
        this.due_fee = due_fee;
        this.paid_date = paid_date;
    }
}
