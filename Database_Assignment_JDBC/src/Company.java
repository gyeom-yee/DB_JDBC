import java.sql.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class Company extends JFrame implements ActionListener {

    public Connection conn;
    public Statement s;
    public ResultSet r;

    public Insert insert;
    private JComboBox Category;
    private JComboBox Dept;
    private JComboBox Sex;
    private JComboBox Update;
    private JTextField setSalary_Bdate_employee = new JTextField(10);

    private JCheckBox c1 = new JCheckBox("Name", true);
    private JCheckBox c2 = new JCheckBox("Ssn", true);
    private JCheckBox c3 = new JCheckBox("Bdate", true);
    private JCheckBox c4 = new JCheckBox("Address", true);
    private JCheckBox c5 = new JCheckBox("Sex", true);
    private JCheckBox c6 = new JCheckBox("Salary", true);
    private JCheckBox c7 = new JCheckBox("Supervisor", true);
    private JCheckBox c8 = new JCheckBox("Department", true);
    private Vector<String> Head = new Vector<String>();

    private JTable table;
    private DefaultTableModel model;
    private static final int BOOLEAN_COLUMN = 0;
    private int NAME_COLUMN = 0;
    private int SALARY_COLUMN = 0;		// salary 업데이트 시 사용
    private int ADDRESS_COLUMN = 0;		// address 업데이트 시 사용
    private int SEX_COLUMN = 0;			// sex 업데이트 시 사
    private String dShow;


    private JButton Search_Button = new JButton("검색");
    Container me = this;

    private JLabel totalEmp = new JLabel("인원수 : ");
    final JLabel totalCount = new JLabel();
    JPanel panel;
    JScrollPane ScPane;
    private JLabel Emplabel = new JLabel("선택한 직원: ");
    private JLabel ShowSelectedEmp = new JLabel();
    private JLabel Setlabel = new JLabel("수정: ");
    private JTextField update_Salary_Address_Sex = new JTextField(10);
    private JButton Update_Button = new JButton("UPDATE");
    private JButton Delete_Button = new JButton("선택한 데이터 삭제");
    private JButton Insert_Button = new JButton("직원 추가");
    int count = 0;
    JPanel ComboBoxPanel = new JPanel();


    public Company() {
    

        String[] category = { "전체", "부서","성별","연봉","생일","부하직원","가족" };
        String[] dept = { "Research", "Administration", "Headquarters" };
        String[] sex = {"F","M"};
        String[] update = {"Address","Sex","Salary"};

        //연봉, 생일, 부하직원은 입력 칸을 만들어준다.
        Category = new JComboBox(category);
        Dept = new JComboBox(dept);
        Sex = new JComboBox(sex);
        Update = new JComboBox(update);

        Category.addActionListener(this);
        Update.addActionListener(this);

        ComboBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ComboBoxPanel.add(new JLabel("검색 범위 "));
        ComboBoxPanel.add(Category);

        //ComboBoxPanel.add(Dept);

        JPanel CheckBoxPanel = new JPanel();
        CheckBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        CheckBoxPanel.add(new JLabel("검색 항목 "));
        CheckBoxPanel.add(c1);
        CheckBoxPanel.add(c2);
        CheckBoxPanel.add(c3);
        CheckBoxPanel.add(c4);
        CheckBoxPanel.add(c5);
        CheckBoxPanel.add(c6);
        CheckBoxPanel.add(c7);
        CheckBoxPanel.add(c8);
        CheckBoxPanel.add(Search_Button);

        JPanel InsertPanel = new JPanel();
        InsertPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        InsertPanel.add(Insert_Button);

        JPanel CheckBoxInsertPanel = new JPanel();
        CheckBoxInsertPanel.setLayout(new BoxLayout(CheckBoxInsertPanel, BoxLayout.X_AXIS));
        CheckBoxInsertPanel.add(CheckBoxPanel);
        CheckBoxInsertPanel.add(InsertPanel);

        JPanel ShowSelectedPanel = new JPanel();
        ShowSelectedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Emplabel.setFont(new Font("Dialog", Font.BOLD, 16));
        ShowSelectedEmp.setFont(new Font("Dialog", Font.BOLD, 16));
        dShow = "";
        ShowSelectedPanel.add(Emplabel);
        ShowSelectedPanel.add(ShowSelectedEmp);

        JPanel TotalPanel = new JPanel();
        TotalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        TotalPanel.add(totalEmp);
        TotalPanel.add(totalCount);

        JPanel UpdatePanel = new JPanel();
        UpdatePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        UpdatePanel.add(Setlabel);
        UpdatePanel.add(Update);
        UpdatePanel.add(update_Salary_Address_Sex);
        UpdatePanel.add(Update_Button);

        JPanel DeletePanel = new JPanel();
        DeletePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        DeletePanel.add(Delete_Button);

        JPanel Top = new JPanel();
        Top.setLayout(new BoxLayout(Top, BoxLayout.Y_AXIS));
        Top.add(ComboBoxPanel);
        Top.add(CheckBoxInsertPanel);

        JPanel Halfway = new JPanel();
        Halfway.setLayout(new BoxLayout(Halfway, BoxLayout.X_AXIS));
        Halfway.add(ShowSelectedPanel);

        JPanel Bottom = new JPanel();
        Bottom.setLayout(new BoxLayout(Bottom, BoxLayout.X_AXIS));
        Bottom.add(TotalPanel);
        Bottom.add(UpdatePanel);
        Bottom.add(DeletePanel);

        JPanel ShowVertical = new JPanel();
        ShowVertical.setLayout(new BoxLayout(ShowVertical, BoxLayout.Y_AXIS));
        ShowVertical.add(Halfway);
        ShowVertical.add(Bottom);

        add(Top, BorderLayout.NORTH);
        add(ShowVertical, BorderLayout.SOUTH);

        Search_Button.addActionListener(this);
        Delete_Button.addActionListener(this);
        Insert_Button.addActionListener(this);
        Update_Button.addActionListener(this);

        setTitle("Company Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        // DB연결
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // JDBC 드라이버 연결

            String user = "root";
            String pwd = "ryan1024@"; // 비밀번호 입력
            String dbname = "mydb";
            String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";

            conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("정상적으로 연결되었습니다.");

        } catch (SQLException e1) {
            System.err.println("연결할 수 없습니다.");
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            System.err.println("드라이버를 로드할 수 없습니다.");
            e1.printStackTrace();
        }

        // ------------------------------------------------------------------------ //

        if (count == 1) {
            me.remove(panel);
            revalidate();
        }

        String category_item = Category.getSelectedItem().toString();

        if(category_item == "부서") {
            if(ComboBoxPanel.getComponentCount() != 2) {
                ComboBoxPanel.remove(2);
            }
            ComboBoxPanel.add(Dept);
            ComboBoxPanel.repaint();
            revalidate();
        }else if(category_item == "성별") {
            if(ComboBoxPanel.getComponentCount() != 2) {
                System.out.println(ComboBoxPanel.getComponentCount());
                ComboBoxPanel.remove(2);
            }
            ComboBoxPanel.add(Sex);
            ComboBoxPanel.repaint();
            revalidate();
        }else if(category_item.equals("연봉") || category_item.equals("생일")
                || category_item.equals("부하직원")){
            if(ComboBoxPanel.getComponentCount() != 2) {
                ComboBoxPanel.remove(2);
            }
            ComboBoxPanel.repaint();
            ComboBoxPanel.add(setSalary_Bdate_employee);
            revalidate();
        }else if(category_item.equals("전체")) {
            if(ComboBoxPanel.getComponentCount() != 2) {
                ComboBoxPanel.remove(2);
            }
            ComboBoxPanel.repaint();
            revalidate();
        }


        if (e.getSource() == Search_Button) {
            String getSalary_Bdate_employee = null;
            if(setSalary_Bdate_employee.getText() != null) {
                getSalary_Bdate_employee = setSalary_Bdate_employee.getText();
            }

            if (c1.isSelected() || c2.isSelected() || c3.isSelected() || c4.isSelected() || c5.isSelected()
                    || c6.isSelected() || c7.isSelected() || c8.isSelected()) {
                Head.clear();
                Head.add("선택");

                String stmt = "select";
                if (c1.isSelected()) {
                    stmt += " concat(e.fname,' ', e.minit,' ', e.lname,' ') as Name";
                    Head.add("NAME");
                }
                if (c2.isSelected()) {
                    if (!c1.isSelected())
                        stmt += " e.ssn";
                    else
                        stmt += ", e.ssn";
                    Head.add("SSN");
                }
                if (c3.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected())
                        stmt += " e.birth";
                    else
                        stmt += ", e.birth";
                    Head.add("BDATE");
                }
                if (c4.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected())
                        stmt += " e.address";
                    else
                        stmt += ", e.address";
                    Head.add("ADDRESS");
                }
                if (c5.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected() && !c4.isSelected())
                        stmt += " e.sex";
                    else
                        stmt += ", e.sex";
                    Head.add("SEX");
                }
                if (c6.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected() && !c4.isSelected()
                            && !c5.isSelected())
                        stmt += " e.salary";
                    else
                        stmt += ", e.salary";
                    Head.add("SALARY");
                }
                if (c7.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected() && !c4.isSelected() && !c5.isSelected()
                            && !c6.isSelected())
                        stmt += " concat(s.fname, ' ', s.minit, ' ',s.lname,' ') as Supervisor ";
                    else
                        stmt += ", concat(s.fname, ' ', s.minit, ' ',s.lname,' ') as Supervisor ";
                    Head.add("SUPERVISOR");
                }
                if (c8.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected() && !c4.isSelected() && !c5.isSelected()
                            && !c6.isSelected() && !c7.isSelected())
                        stmt += " dname";
                    else
                        stmt += ", dname";
                    Head.add("DEPARTMENT");
                }
                stmt += " from employee e left outer join employee s on e.super_ssn=s.ssn, department d where e.dno = dnumber";

                if (Category.getSelectedItem().toString() == "부서") {

                    if (Dept.getSelectedItem().toString() == "Research")
                        stmt += " and dname = \"Research\";";
                    else if (Dept.getSelectedItem().toString() == "Administration")
                        stmt += " and dname = \"Administration\";";
                    else if (Dept.getSelectedItem().toString() == "Headquarters")
                        stmt += " and dname = \"Headquarters\";";
                }else if(Category.getSelectedItem().toString() == "성별") {
                    if(Sex.getSelectedItem().toString() == "F") {
                        stmt += " and e.SEX = \"F\";";
                    }else if(Sex.getSelectedItem().toString() == "M") {
                        stmt += " and e.SEX = \"M\";";
                    }
                }else if(Category.getSelectedItem().toString() == "연봉") {
                    stmt += " and e.salary >=" +getSalary_Bdate_employee + ";";
                    setSalary_Bdate_employee.setText("");
                }else if(Category.getSelectedItem().toString() == "생일") {
                    stmt += " and e.birth like \"____-" + getSalary_Bdate_employee + "%\";";
                    setSalary_Bdate_employee.setText("");
                }else if(Category.getSelectedItem().toString() == "부하직원") {
                    String[] name = getSalary_Bdate_employee.split(" ");
                    String fname = name[0];
                    String minit = name[1];
                    String lname = name[2];
                    stmt += " and s.fname = \"" +fname + "\""+
                            " and s.minit = \"" + minit + "\""+
                            " and s.lname = \"" + lname + "\"" + ";";
                }
                else if(Category.getSelectedItem().toString() == "가족") {
                    stmt = "select fname,dependent_name from employee,dependent d where d.essn=ssn";
                }
                
                

                model = new DefaultTableModel(Head, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        if (column > 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                };
                for (int i = 0; i < Head.size(); i++) {
                    if (Head.get(i) == "NAME") {
                        NAME_COLUMN = i;
                    } else if (Head.get(i) == "SALARY") {
                        SALARY_COLUMN = i;
                    }else if (Head.get(i) == "ADDRESS") {
                        ADDRESS_COLUMN = i;
                    }else if (Head.get(i) == "SEX") {
                        SEX_COLUMN = i;
                    }
                }
                table = new JTable(model) {
                    @Override
                    public Class getColumnClass(int column) {
                        if (column == 0) {
                            return Boolean.class;
                        } else
                            return String.class;
                    }
                };

                ShowSelectedEmp.setText(" ");

                try {

                    count = 1;
                    s = conn.createStatement();
                    r = s.executeQuery(stmt);
                    ResultSetMetaData rsmd = r.getMetaData();
                    int columnCnt = rsmd.getColumnCount();
                    int rowCnt = table.getRowCount();

                    while (r.next()) {
                        Vector<Object> tuple = new Vector<Object>();
                        tuple.add(false);
                        for (int i = 1; i < columnCnt + 1; i++) {
                            tuple.add(r.getString(rsmd.getColumnName(i)));
                        }
                        model.addRow(tuple);
                        rowCnt++;
                    }
                    totalCount.setText(String.valueOf(rowCnt));

                } catch (SQLException ee) {
                    System.out.println("actionPerformed err : " + ee);
                    ee.printStackTrace();

                }
                panel = new JPanel();
                ScPane = new JScrollPane(table);
                table.getModel().addTableModelListener(new CheckBoxModelListener());
                ScPane.setPreferredSize(new Dimension(1100, 400));
                panel.add(ScPane);
                add(panel, BorderLayout.CENTER);
                revalidate();

            } else {
                JOptionPane.showMessageDialog(null, "검색 항목을 한개 이상 선택하세요.");
            }

        }
        // DELETE
        if (e.getSource() == Delete_Button) {
            Vector<String> delete_ssn = new Vector<String>();

            try {

                String columnName = model.getColumnName(2);
                if (columnName == "SSN") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            delete_ssn.add((String) table.getValueAt(i, 2));
                        }
                    }
                    for (int i = 0; i < delete_ssn.size(); i++) {
                        for (int k = 0; k < model.getRowCount(); k++) {
                            if (table.getValueAt(k, 0) == Boolean.TRUE) {
                                model.removeRow(k);
                                totalCount.setText(String.valueOf(table.getRowCount()));
                            }
                        }
                    }
                    for (int i = 0; i < delete_ssn.size(); i++) {
                        String deleteStmt = "DELETE FROM EMPLOYEE WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(deleteStmt);
                        p.clearParameters();
                        p.setString(1, String.valueOf(delete_ssn.get(i)));
                        p.executeUpdate();

                    }
                } else {
                    JOptionPane.showMessageDialog(null, "삭제 작업을 진행하시려면 NAME, SSN 항목을 모두 체크해주세요.");
                }

                ShowSelectedEmp.setText(" ");

            } catch (SQLException e1) {
                System.out.println("actionPerformed err : " + e1);
                e1.printStackTrace();
            }
            panel = new JPanel();
            ScPane = new JScrollPane(table);
            table.getModel().addTableModelListener(new CheckBoxModelListener());
            ScPane.setPreferredSize(new Dimension(1100, 400));
            panel.add(ScPane);
            add(panel, BorderLayout.CENTER);
            revalidate();

        } // DELETE 끝

        // UPDATE

        if (e.getSource() == Update_Button) {

            Vector<String> update_ssn = new Vector<String>();
            try {
                String columnName = model.getColumnName(2);

                System.out.println(columnName);
                if (columnName == "SSN") {
                    if(Update.getSelectedItem().toString() == "Salary") {
                        for (int i = 0; i < table.getRowCount(); i++) {
                            if (table.getValueAt(i, 0) == Boolean.TRUE) {
                                String updateSalary = update_Salary_Address_Sex.getText();
                                table.setValueAt(Double.parseDouble(updateSalary), i, SALARY_COLUMN);
                                String updateStmt = "UPDATE EMPLOYEE SET Salary=? WHERE Ssn=?";
                                PreparedStatement p = conn.prepareStatement(updateStmt);
                                p.clearParameters();
                                p.setString(1, updateSalary);
                                p.setString(2, String.valueOf((String) table.getValueAt(i, 2)));
                                p.executeUpdate();
                            }
                        }
                    }else if(Update.getSelectedItem().toString() == "Sex") {
                        for (int i = 0; i < table.getRowCount(); i++) {
                            if (table.getValueAt(i, 0) == Boolean.TRUE) {
                                //update_ssn.add((String) table.getValueAt(i, 2));
                                String updateSalary = update_Salary_Address_Sex.getText();
                                table.setValueAt(updateSalary, i, SEX_COLUMN);
                                String updateStmt = "UPDATE EMPLOYEE SET SEX=? WHERE Ssn=?";
                                PreparedStatement p = conn.prepareStatement(updateStmt);
                                p.clearParameters();
                                p.setString(1, String.valueOf(updateSalary));
                                p.setString(2, String.valueOf((String) table.getValueAt(i, 2)));
                                p.executeUpdate();
                            }
                        }
                    }else if(Update.getSelectedItem().toString() == "Address") {
                        for (int i = 0; i < table.getRowCount(); i++) {
                            if (table.getValueAt(i, 0) == Boolean.TRUE) {
                                //update_ssn.add((String) table.getValueAt(i, 2));
                                String updateSalary = update_Salary_Address_Sex.getText();
                                table.setValueAt(updateSalary, i, ADDRESS_COLUMN);
                                String updateStmt = "UPDATE EMPLOYEE SET Address=? WHERE Ssn=?";
                                PreparedStatement p = conn.prepareStatement(updateStmt);
                                p.clearParameters();
                                p.setString(1, String.valueOf(updateSalary));
                                p.setString(2, String.valueOf((String) table.getValueAt(i, 2)));
                                p.executeUpdate();
                            }
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "수정 작업을 위해 Name과 SSN열을 체크해주시기 바랍니다.");
                }

                ShowSelectedEmp.setText(" ");

            } catch (SQLException e1) {
                System.out.println("actionPerformed err : " + e1);
                e1.printStackTrace();
            }
            panel = new JPanel();
            ScPane = new JScrollPane(table);
            table.getModel().addTableModelListener(new CheckBoxModelListener());
            ScPane.setPreferredSize(new Dimension(1100, 400));
            panel.add(ScPane);
            add(panel, BorderLayout.CENTER);
            revalidate();
        } // UPDATE 끝

        // insert 시
        if(e.getSource() == Insert_Button) {
            insert = new Insert();
            insert.button.addActionListener(this);
        }

        if(this.insert != null) {
            if(e.getSource() == insert.button) {
                String text_FirstName,text_MiddleInit,text_LastName,text_Ssn,
                        text_Birthdate,text_Address,box_Sex,text_Salary,text_Super_ssn,text_Dno = "NULL";

                text_FirstName = "'" + insert.text_FirstName.getText()+"'";
                text_MiddleInit = "'" + insert.text_MiddleInit.getText()+"'";
                text_LastName = "'" + insert.text_LastName.getText() + "'";
                text_Ssn = "'" + insert.text_Ssn.getText() + "'";
                text_Birthdate = "'" + insert.text_Birthdate.getText() + "'";
                text_Address = "'" + insert.text_Address.getText() + "'";
                box_Sex = "'" + insert.box_Sex.getSelectedItem().toString() + "'";
                text_Salary = insert.text_Salary.getText();
                text_Super_ssn = "'" + insert.text_Super_ssn.getText() + "'";
                text_Dno = insert.text_Dno.getText();

                if(text_MiddleInit.equals("'" + "'")) {
                    text_MiddleInit = "null";
                }else if(text_Birthdate.equals("'" + "'")) {
                    text_Birthdate = "null";
                }else if(text_Address.equals("'" + "'")) {
                    text_Address = "null";
                }else if(text_Salary.equals("'" + "'")) {
                    text_Salary = "null";
                }else if(text_Super_ssn.equals("'" + "'")) {
                    text_Super_ssn = "null";
                }else if(text_Dno.equals("'" + "'")) {
                    text_Dno = "null";
                }
                
                if( isStringEmpty(text_FirstName) || isStringEmpty(text_LastName) ||
                        isStringEmpty(text_Ssn) || isStringEmpty(text_Dno)) {
                    JOptionPane.showMessageDialog(null, "FirstName, LastName,Ssn,Dno는 비어있으면 안됩니다.");
                }else {
                    String sql = "insert into Employee(Fname,Minit,Lname,Ssn,Birth,Address,Sex,"
                            + "Salary,Super_ssn,Dno) "
                            + "values("+text_FirstName+","+
                            text_MiddleInit+","+
                            text_LastName+","+
                            text_Ssn+","+
                            text_Birthdate+","+
                            text_Address+","+
                            box_Sex+","+
                            Integer.parseInt(text_Salary)+","+
                            text_Super_ssn+","+
                            Integer.parseInt(text_Dno)+");";

                    try {
                        Statement s = conn.createStatement();
                        int result = s.executeUpdate(sql);
                        insert.dispose();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
    static boolean isStringEmpty(String str) {
        return str == null || str.isEmpty();
    }
    // insert

    public class CheckBoxModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == BOOLEAN_COLUMN) {
                TableModel model = (TableModel) e.getSource();
                String columnName = model.getColumnName(1);
                Boolean checked = (Boolean) model.getValueAt(row, column);
                if (columnName == "NAME") {
                    if (checked) {
                        dShow = "";
                        for (int i = 0; i < table.getRowCount(); i++) {
                            if (table.getValueAt(i, 0) == Boolean.TRUE) {
                                dShow += (String) table.getValueAt(i, NAME_COLUMN) + "    ";

                            }
                        }
                        ShowSelectedEmp.setText(dShow);
                    } else {
                        dShow = "";
                        for (int i = 0; i < table.getRowCount(); i++) {
                            if (table.getValueAt(i, 0) == Boolean.TRUE) {
                                dShow += (String) table.getValueAt(i, 1) + "    ";
                            }
                        }
                        ShowSelectedEmp.setText(dShow);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new Company();
    }
}