package model;

public class Department {
    private String deptNo;
    private String name;

    public Department() {
    }

    public Department(String deptNo, String name) {
        this.deptNo = deptNo;
        this.name = name;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
