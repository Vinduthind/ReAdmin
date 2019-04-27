package techxpose.co.allresultadmin.Model;

public class ShowUpdateModel {
    public   ShowUpdateModel(){}



    private String branchname;
    private String resultlink;
    private String year;
    private String date;
    private String resultdate;
    private String examination;
    private String name;
    private String course;

    public ShowUpdateModel(String branchname, String resultlink, String year, String date, String resultDate, String examination) {
        this.branchname = branchname;
        this.resultlink = resultlink;
        this.year = year;
        this.date = date;
        this.resultdate = resultDate;
        this.examination = examination;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getResultDate() {
        return resultdate;
    }

    public void setResultDate(String resultDate) {
        this.resultdate = resultDate;
    }

    public String getExamination() {
        return examination;
    }

    public void setExamination(String examination) {
        this.examination = examination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBranchname() {
        return branchname+".";
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public void setResultlink(String resultlink) {
        this.resultlink = resultlink;
    }

    public String getResultlink() {
        return resultlink;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {       this.year = year;    }

}
