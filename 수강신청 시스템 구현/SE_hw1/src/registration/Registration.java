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
	public ArrayList<String> pastSubject = new ArrayList<String>(); //이전 수강정보 담을 arrayList
	public ArrayList<String> presentSubject = new ArrayList<String>(); //현재 신청정보 담을 arrayList

	StudentInfo(String id, String name, ArrayList<String> pastSubject){
		this.id = id;
		this.name = name;
		this.pastSubject = pastSubject;
	}
	public void saveInfo() {//학생객체를 테이블로 저장하기 위한 메소드
		OraJava d = new OraJava();
		d.load();
		try 
		{
			String sql = "CREATE TABLE "+this.id+" (PASTSBJ VARCHAR2(100), PRESENTSBJ VARCHAR2(100))";
			Statement stmt = d.conn.createStatement();
			stmt.execute(sql);
			System.out.println("테이블이 생성되었습니다");
			for(String sbj:this.pastSubject) {
				sql = "INSERT INTO "+this.id+" (PASTSBJ,PRESENTSBJ) VALUES('"+sbj+"','')";
				Statement stmt2 = d.conn.createStatement();
				stmt2.execute(sql);
			}

			stmt.close();
			System.out.println("학생정보가 저장되었습니다");
		}
		catch (SQLException e) 
		{	
			System.out.println("이미 학생정보가 존재합니다");
		}
		d.close();
	}
	
	public ArrayList<String> doRegit() {//수강신청과목을 입력받아 리턴
		System.out.println("수강신청을 시작합니다");
		ArrayList<String> tempSbj = new ArrayList<String>();
		OraJava d = new OraJava();
		d.load();
		
		while(true) 
		{	
			try 
			{	
				System.out.println("신청할 과목코드와 분반을 공백없이 입력하세요");
				System.out.println("입력이 끝났으면 'exit'을 입력하세요");
				Scanner scan = new Scanner(System.in);
				String sbj = scan.next().toUpperCase();
				//scan.close();
				
				if(sbj.equals("EXIT")) {
					System.out.println("입력이 끝났습니다");
					break;
				}
				if(sbj.length()!= 9) {
					System.out.println("신청과목을 바르게 입력하세요\n");
					continue;
				}
				String sql = "SELECT * FROM SUBJECT WHERE 과목코드='"+sbj.substring(0,7)+"' AND 분반='"+sbj.substring(7,9)+"'";
				Statement stmt = d.conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				if(!rs.next()) {
					throw new SQLException();
				}
				
				stmt.close();
				
				tempSbj.add(sbj);
				System.out.println(sbj+"과목이 추가되었습니다\n");
			}
			catch (SQLException e) 
			{	
				System.out.println("신청과목을 바르게 입력하세요\n");
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
			System.out.println("신청과목이 저장되었습니다\n");
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
		System.out.println("신청 교과목을 조회합니다\n");
		try
		{
			String sql = "SELECT PRESENTSBJ FROM "+this.id;
			Statement stmt = d.conn.createStatement(); //Statement 객체생성
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()) { //리턴 데이터 처리
				String sbj = rs.getString(1);
				if(sbj!=null) {
					System.out.printf("과목코드와 분반: %s\n",sbj);
				}
			}	
			rs.close(); //객체 close
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
	public static void showSbj() {// 개설교과목 조회 위한 메소드
		OraJava d = new OraJava();
		d.load();
		try
		{	
			System.out.println("개설 교과목을 조회합니다");
			String sql = "SELECT * FROM SUBJECT";
			Statement stmt = d.conn.createStatement(); //Statement 객체생성
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()) { //리턴 데이터 처리
				System.out.printf("과목코드 : %s,분반 : %2d,과목명 : %s,강의시간 : %s,강의실 : %s,담당교수 : %s,선수과목 : %s\n",rs.getString(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7) );

			}
			rs.close(); //객체 close
			stmt.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		d.close();

	}
}


class OraJava // Oracle DB와의 연동위한 클래스
{
	static String url = null;
	static String id = "ck";
	static String pw = "1234";
	Connection conn = null;

	public void load() {
		try //JDBC 드라이버 로드
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
	public void close() {//DB connection close위한 메소드
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
		//17년 수강신청 과목 정보를 포함해 StudentInfo 객체 생성
		String[] tmpSbj= {"MAT1050","MAT3020","MAT3430","ECO2009","ECO3011","ECO3021","MAT1060","STS2006","CSW2020","CSW2030","MAT4331","CSW3030","ECO4033","MGT4206"};
		ArrayList<String> past0602 = new ArrayList<String>();
		for (String s:tmpSbj)past0602.add(s);

		StudentInfo s1 = new StudentInfo("s20130602","백찬규",past0602);
		s1.saveInfo(); // 학생정보를 db에 저장

		ShowSbj.showSbj(); //개설 교과목 조회



		while(true) {
			ArrayList<String> tempSbj = s1.doRegit(); // 수강신청과목 저장한 임시 리스트 
			ArrayList<String> tempTime = new ArrayList<String>(); // 강의시간을 불러올 임시 리스트
			ArrayList<String> tempPrerequisite = new ArrayList<String>(); // 선수과목을 불러올 임시리스트

			Boolean OverlapOK = true; // 강의시간 중복여부 체크
			Boolean prerequisiteOK = true; // 선수과목 이수 여부 체크

			OraJava d = new OraJava();
			d.load();

			for(String s:tempSbj) {

				try 
				{
					String sql = "SELECT 과목코드,분반,강의시간,선수과목 FROM SUBJECT WHERE 과목코드='"+s.substring(0,7)+"' AND 분반='"+s.substring(7,9)+"'";
					Statement stmt = d.conn.createStatement(); //Statement 객체생성
					ResultSet rs = stmt.executeQuery(sql);

					while(rs.next()) { //리턴 데이터 처리
						System.out.printf("과목코드 : %s,분반 : %s\n",rs.getString(1),rs.getString(2) );
						tempTime.add(rs.getString(3));
						tempPrerequisite.add(rs.getString(4));
					}
					rs.close(); //객체 close
					stmt.close();
				}
				catch(SQLException e)
				{	
					e.printStackTrace();
				}
			}

			if(tempSbj.size()>8 ||tempSbj.size()<3 ) { // 최소 9학점 최대 24학점 체크
				System.out.println("신청가능학점을 확인하세요 수강신청을 다시시작합니다");
				continue;
			}
			for(int i=0;i<tempTime.size();i++) {
				for(int j = i+1;j<tempTime.size();j++) {
					String a = tempTime.get(i);
					String b = tempTime.get(j);
					if(a.equals(b)) {
						OverlapOK=false;  // 신청과목 중 동일한 강의시간 있을경우 
					}
				}
			}
			for(int i=0;i<tempPrerequisite.size();i++) {//선수과목이수여부 점검
				try {
					String[] pre = tempPrerequisite.get(i).split(",");//선수과목코드를 저장한 배열 pre
					for(String s :pre) { // 중첩된 반복문 돌며 선수과목 이수 여부를 과목마다 점검
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
				System.out.println("강의시간 중복입니다 수강신청을 다시시작합니다\n");
				continue;
			}
			if(prerequisiteOK==false) { 
				System.out.println("선수과목을 확인하세요 수강신청을 다시시작합니다\n");
				continue;
			}
			d.close();
			s1.presentSubject = tempSbj;
			break;
		}
		s1.savePresentSbj(); //학생정보에 신청교과목 저장


		s1.showMysbj(); //신청 교과목 조회

		System.out.println("시스템을 종료합니다");
		System.exit(0);


	}

}
