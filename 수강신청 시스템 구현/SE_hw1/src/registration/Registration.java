package registration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

class StudentInfo{
	public String id;
	public String name;
	public ArrayList<String> pastSubject = new ArrayList<String>(); //���� �������� ���� arrayList
	public ArrayList<String> presentSubject = new ArrayList<String>(); //���� ��û���� ���� arrayList

	StudentInfo(String id, String name, ArrayList<String> pastSubject){
		this.id = id;
		this.name = name;
		this.pastSubject = pastSubject;
	}
	public void saveInfo() {//�л���ü�� ���̺�� �����ϱ� ���� �޼ҵ�
		OraJava d = new OraJava();
		d.load();
		try 
		{
			String sql = "CREATE TABLE "+this.id+" (PASTSBJ VARCHAR2(100), PRESENTSBJ VARCHAR2(100))";
			Statement stmt = d.conn.createStatement();
			stmt.execute(sql);
			System.out.println("���̺��� �����Ǿ����ϴ�");
			for(String sbj:this.pastSubject) {
				sql = "INSERT INTO "+this.id+" (PASTSBJ,PRESENTSBJ) VALUES('"+sbj+"','')";
				Statement stmt2 = d.conn.createStatement();
				stmt2.execute(sql);
			}

			stmt.close();
			System.out.println("�л������� ����Ǿ����ϴ�");
		}
		catch (SQLException e) 
		{	
			System.out.println("�̹� �л������� �����մϴ�");
		}
		d.close();
	}
	
	public ArrayList<String> doRegit() {//������û������ �Է¹޾� ����
		System.out.println("������û�� �����մϴ�");
		ArrayList<String> tempSbj = new ArrayList<String>();
		OraJava d = new OraJava();
		d.load();
		
		while(true) 
		{	
			try 
			{	
				System.out.println("��û�� �����ڵ�� �й��� ������� �Է��ϼ���");
				System.out.println("�Է��� �������� 'exit'�� �Է��ϼ���");
				Scanner scan = new Scanner(System.in);
				String sbj = scan.next().toUpperCase();
				//scan.close();
				
				if(sbj.equals("EXIT")) {
					System.out.println("�Է��� �������ϴ�");
					break;
				}
				if(sbj.length()!= 9) {
					System.out.println("��û������ �ٸ��� �Է��ϼ���\n");
					continue;
				}
				String sql = "SELECT * FROM SUBJECT WHERE �����ڵ�='"+sbj.substring(0,7)+"' AND �й�='"+sbj.substring(7,9)+"'";
				Statement stmt = d.conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				if(!rs.next()) {
					throw new SQLException();
				}
				
				stmt.close();
				
				tempSbj.add(sbj);
				System.out.println(sbj+"������ �߰��Ǿ����ϴ�\n");
			}
			catch (SQLException e) 
			{	
				System.out.println("��û������ �ٸ��� �Է��ϼ���\n");
				continue;
			}
			

			
			

		}
		System.out.println();
		return tempSbj;
	}
	public void savePresentSbj() {
		OraJava d = new OraJava();
		d.load();
		try 
		{
			for(String sbj:this.presentSubject) {
				String sql = "INSERT INTO "+this.id+" (PASTSBJ,PRESENTSBJ) VALUES('','"+sbj+"')";
				Statement stmt = d.conn.createStatement();
				stmt.execute(sql);
			}
			System.out.println("��û������ ����Ǿ����ϴ�\n");
		}
		catch (SQLException e) 
		{	
			e.printStackTrace();
		}
		d.close();
	}

	public void showMysbj() {
		OraJava d = new OraJava();
		d.load();
		System.out.println("��û �������� ��ȸ�մϴ�\n");
		try
		{
			String sql = "SELECT PRESENTSBJ FROM "+this.id;
			Statement stmt = d.conn.createStatement(); //Statement ��ü����
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()) { //���� ������ ó��
				String sbj = rs.getString(1);
				if(sbj!=null) {
					System.out.printf("�����ڵ�� �й�: %s\n",sbj);
				}
			}	
			rs.close(); //��ü close
			stmt.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		d.close();
	}
}

class ShowSbj{
	public static void showSbj() {// ���������� ��ȸ ���� �޼ҵ�
		OraJava d = new OraJava();
		d.load();
		try
		{	
			System.out.println("���� �������� ��ȸ�մϴ�");
			String sql = "SELECT * FROM SUBJECT";
			Statement stmt = d.conn.createStatement(); //Statement ��ü����
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()) { //���� ������ ó��
				System.out.printf("�����ڵ� : %s,�й� : %2d,����� : %s,���ǽð� : %s,���ǽ� : %s,��米�� : %s,�������� : %s\n",rs.getString(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7) );

			}
			rs.close(); //��ü close
			stmt.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		d.close();

	}
}


class OraJava // Oracle DB���� �������� Ŭ����
{
	static String url = null;
	static String id = "ck";
	static String pw = "1234";
	Connection conn = null;

	public void load() {
		try //JDBC ����̹� �ε�
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//System.out.println("driver load success");

			try // DB Connection
			{
				url = "jdbc:oracle:thin:@localhost:1521:orcl";
				this.conn = DriverManager.getConnection(url,id,pw);
				//System.out.println("db connect success");
			}
			catch (SQLException e)
			{
				System.out.println(e);
			}
		}
		catch (ClassNotFoundException e)
		{
			System.out.println(e);
		}
	}
	public void close() {//DB connection close���� �޼ҵ�
		try
		{
			this.conn.close(); 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}


public class Registration {
	public static void main(String[] args) {
		//17�� ������û ���� ������ ������ StudentInfo ��ü ����
		String[] tmpSbj= {"MAT1050","MAT3020","MAT3430","ECO2009","ECO3011","ECO3021","MAT1060","STS2006","CSW2020","CSW2030","MAT4331","CSW3030","ECO4033","MGT4206"};
		ArrayList<String> past0602 = new ArrayList<String>();
		for (String s:tmpSbj)past0602.add(s);

		StudentInfo s1 = new StudentInfo("s20130602","������",past0602);
		s1.saveInfo(); // �л������� db�� ����

		ShowSbj.showSbj(); //���� ������ ��ȸ



		while(true) {
			ArrayList<String> tempSbj = s1.doRegit(); // ������û���� ������ �ӽ� ����Ʈ 
			ArrayList<String> tempTime = new ArrayList<String>(); // ���ǽð��� �ҷ��� �ӽ� ����Ʈ
			ArrayList<String> tempPrerequisite = new ArrayList<String>(); // ���������� �ҷ��� �ӽø���Ʈ

			Boolean OverlapOK = true; // ���ǽð� �ߺ����� üũ
			Boolean prerequisiteOK = true; // �������� �̼� ���� üũ

			OraJava d = new OraJava();
			d.load();

			for(String s:tempSbj) {

				try 
				{
					String sql = "SELECT �����ڵ�,�й�,���ǽð�,�������� FROM SUBJECT WHERE �����ڵ�='"+s.substring(0,7)+"' AND �й�='"+s.substring(7,9)+"'";
					Statement stmt = d.conn.createStatement(); //Statement ��ü����
					ResultSet rs = stmt.executeQuery(sql);

					while(rs.next()) { //���� ������ ó��
						System.out.printf("�����ڵ� : %s,�й� : %s\n",rs.getString(1),rs.getString(2) );
						tempTime.add(rs.getString(3));
						tempPrerequisite.add(rs.getString(4));
					}
					rs.close(); //��ü close
					stmt.close();
				}
				catch(SQLException e)
				{	
					e.printStackTrace();
				}
			}

			if(tempSbj.size()>8 ||tempSbj.size()<3 ) { // �ּ� 9���� �ִ� 24���� üũ
				System.out.println("��û���������� Ȯ���ϼ��� ������û�� �ٽý����մϴ�");
				continue;
			}
			for(int i=0;i<tempTime.size();i++) {
				for(int j = i+1;j<tempTime.size();j++) {
					String a = tempTime.get(i);
					String b = tempTime.get(j);
					if(a.equals(b)) {
						OverlapOK=false;  // ��û���� �� ������ ���ǽð� ������� 
					}
				}
			}
			for(int i=0;i<tempPrerequisite.size();i++) {//���������̼����� ����
				try {
					String[] pre = tempPrerequisite.get(i).split(",");//���������ڵ带 ������ �迭 pre
					for(String s :pre) { // ��ø�� �ݺ��� ���� �������� �̼� ���θ� ���񸶴� ����
						prerequisiteOK=false;
						for(String pstSbj:s1.pastSubject) {
							if(s.equals(pstSbj)) {
								prerequisiteOK=true;
								break;
							}
						}
					}
				}
				catch(Exception e){

				}

			}
			if (OverlapOK==false){ 
				System.out.println("���ǽð� �ߺ��Դϴ� ������û�� �ٽý����մϴ�\n");
				continue;
			}
			if(prerequisiteOK==false) { 
				System.out.println("���������� Ȯ���ϼ��� ������û�� �ٽý����մϴ�\n");
				continue;
			}
			d.close();
			s1.presentSubject = tempSbj;
			break;
		}
		s1.savePresentSbj(); //�л������� ��û������ ����


		s1.showMysbj(); //��û ������ ��ȸ

		System.out.println("�ý����� �����մϴ�");
		System.exit(0);


	}

}
