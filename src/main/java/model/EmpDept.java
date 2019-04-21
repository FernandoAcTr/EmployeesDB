package model;

public class EmpDept {
    private int pmpNo;
    private String deptNo;

    public EmpDept() {
    }

    public EmpDept(int pmpNo, String deptNo) {
        this.pmpNo = pmpNo;
        this.deptNo = deptNo;
    }

    public int getPmpNo() {
        return pmpNo;
    }

    public void setPmpNo(int pmpNo) {
        this.pmpNo = pmpNo;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }
}
