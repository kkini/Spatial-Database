import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Hw2 {
	public static void main(String[] args) throws SQLException {
		 
		System.out.println("-------- Oracle JDBC Connection Testing ------");
 
		try {
 
			Class.forName("oracle.jdbc.driver.OracleDriver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return;
 
		}
 
		System.out.println("Oracle JDBC Driver Registered!");
 
		Connection connection = null;
		PreparedStatement pstatement = null;
		ResultSet rSet = null;
 
		try {
 
			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:orcl1", "hr",
					"orcl1");
 
		} catch (SQLException e) {
 
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
 
		}
 
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		System.out.println("---------------------Query Output--------------------------");
		//System.out.println("Enter query type :window, within, nearest-neighbor or fixed \n");
		 
		/*try{
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String queryType = bufferRead.readLine();
	 
		    System.out.println(queryType);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}*/
		//********************Query 1*****************************************
		String queryType = args[0];
		
		if(queryType.equals("window"))
		{
			
			String id = "";
			String objectType ="";
			String vertices = "";
			
			if(args[1].equalsIgnoreCase("student"))
			{
				id = "student_id";
				objectType = "student s";
				vertices = args[2]+","+args[3]+","+args[4]+","+args[5];
				
			}else if(args[1].equalsIgnoreCase("building"))
			{
				id = "building_id";
				objectType = "building s";
				vertices = args[2]+","+args[3]+","+args[4]+","+args[5];
				
			}else{
				id = "tramstation_id";
				objectType = "tramstop s";
				vertices = args[2]+","+args[3]+","+args[4]+","+args[5];
			}
			String query = "select "+id+" from "+objectType+" where SDO_INSIDE(s.shape,"+
	            			"SDO_GEOMETRY(2003, NULL, NULL,"+
	            			"SDO_ELEM_INFO_ARRAY(1,1003,3),"+
	            			"SDO_ORDINATE_ARRAY("+ vertices +"))"+
	            			") = 'TRUE'";
			try {
				
				pstatement = connection.prepareStatement(query);
				rSet = pstatement.executeQuery();
				String student = null;
				while(rSet.next()){
					student = rSet.getString(1);
					System.out.println(student);
					//System.out.println("\n");
				}
			} catch (SQLException e) {
				
			}finally{
				pstatement.close();
				connection.close();
			}
			
	}
		//********************Query 1*****************************************End
		//********************Query 2*****************************************Start
		if(queryType.equals("within")){
			String student_id = args[1];
			String distance = args[2];
			String query1 = "SELECT t.TRAMSTATION_ID FROM TRAMSTOP t,student s WHERE s.student_id='"+ student_id +"' " +
					"and SDO_WITHIN_DISTANCE(t.shape,s.shape,"+
					"'distance="+ distance +"') = 'TRUE' ";
			String query2 = "SELECT b.building_id FROM building b,student s WHERE s.student_id='"+ student_id +"' and " +
					"SDO_WITHIN_DISTANCE(b.shape,s.shape,'distance="+ distance +"') = 'TRUE'";
			try {
				
				pstatement = connection.prepareStatement(query1);
				rSet = pstatement.executeQuery();
				String id = null;
				System.out.println("Id's of tramstop:\n");
				while(rSet.next()){
					id = rSet.getString(1);
					System.out.println(id);
					
				}
				pstatement = connection.prepareStatement(query2);
				rSet = pstatement.executeQuery();
				System.out.println("Id's of building:\n");
				while(rSet.next()){
					id = rSet.getString(1);
					System.out.println(id);
				
				}
			} catch (SQLException e) {
				
			}finally{
				pstatement.close();
				connection.close();
			}
		}
		//********************Query 2*****************************************End
		//********************Query 3*****************************************
		if(queryType.equals("nearest-neighbor"))
		{
			
			String query= "";
			int number= Integer.parseInt(args[3]);
			
			if(args[1].equalsIgnoreCase("student"))
			{
				int number1 = number+1;
				 /*query = "SELECT s.student_id FROM student s,building b WHERE"+ 
								" SDO_NN(s.shape,b.shape, 'sdo_num_res="+number+"') = 'TRUE'and b.building_id='"+args[2]+"'";*/
				query = "SELECT s1.student_id FROM student s1,student s2 WHERE"+
						" SDO_NN(s1.shape,s2.shape, 'sdo_num_res="+number1+"') = 'TRUE'and s2.student_id='"+ args[2] +"' and s1.student_id<>'"+ args[2] +"'";
				
			}else if(args[1].equalsIgnoreCase("building"))
			{
				int number1 = number+1;
				query ="SELECT b1.building_id FROM building b1,building b2 WHERE"+ 
	   			  " SDO_NN(b1.shape,b2.shape, 'sdo_num_res="+ number1 +"') = 'TRUE'and  b2.building_id='"+ args[2] +"'"+ 
	   			  " and b1.building_id <>'"+ args[2] +"'";
				
				
			}else{
				int number1 = number+1;
				/*query = "SELECT t.tramstation_id FROM tramstop t,building b WHERE"+ 
						" SDO_NN(t.shape,b.shape, 'sdo_num_res="+number+"') = 'TRUE'and  b.building_id='"+ args[2] +"'";*/	
				query = "SELECT t1.tramstation_id FROM tramstop t1,tramstop t2 WHERE"+ 
						" SDO_NN(t1.shape,t2.shape, 'sdo_num_res="+ number1 +"') = 'TRUE'and t2.tramstation_id='"+ args[2] +"' and t1.tramstation_id<>'"+ args[2] +"'";
				
			}
			//String query = "SELECT "+ id + "FROM"+ objectType +",building b1 WHERE"+ 
			   				//"SDO_NN("+prefix+".shape,b1.shape, 'sdo_num_res="+ Integer.parseInt(args[3]) +"') = 'TRUE'and  b1.building_id='"+args[2]+"'";
			
			/*int num = Integer.parseInt(args[3])+1;
			String building_id = args[2];
			String query ="SELECT b1.building_id FROM building b1,building b2 WHERE"+ 
			   			  " SDO_NN(b1.shape,b2.shape, 'sdo_num_res="+ num +"') = 'TRUE'and  b2.building_id='"+ building_id +"'"+ 
			   			  " and b1.building_id <>'"+ building_id +"'";*/
			//System.out.println(query);
			try {
				pstatement = connection.prepareStatement(query);
				rSet = pstatement.executeQuery();
				String student = null;
				while(rSet.next()){
					student = rSet.getString(1);
					System.out.println(student);
			
				}
			} catch (SQLException e) {
				
			}finally{
				pstatement.close();
				connection.close();
			}
		}
		//********************Query 3*****************************************End
		//********************Query 4*****************************************
		if(queryType.equals("fixed")){
			if(args[1].equalsIgnoreCase("1")){
				 /*String query = "select s.student_id from student s, tramstop t"
						+ " WHERE SDO_FILTER(s.shape,sdo_geometry(2003,NULL,NULL,"
						+ "sdo_elem_info_array(1,1003,4),"
						+ "sdo_ordinate_array(t.shape.sdo_point.x+t.tram_radius,t.shape.sdo_point.y,"
						+ "t.shape.sdo_point.x,t.shape.sdo_point.y+t.tram_radius,"
						+ "t.shape.sdo_point.x-t.tram_radius,t.shape.sdo_point.y"
						+ " ))) = 'TRUE' and t.tramstation_id in ('t2ohe','t6ssl')"
						+ "INTERSECT "
						+ "select b.building_id from building b, tramstop t "
						+ "WHERE SDO_FILTER(b.shape,sdo_geometry(2003,NULL,NULL,"
						+ "sdo_elem_info_array(1,1003,4),"
						+ "sdo_ordinate_array(t.shape.sdo_point.x+t.tram_radius,t.shape.sdo_point.y, t.shape.sdo_point.x,"
						+ "t.shape.sdo_point.y+t.tram_radius,"
						+ "t.shape.sdo_point.x-t.tram_radius,t.shape.sdo_point.y))) = 'TRUE' and t.tramstation_id in ('t2ohe','t6ssl')";*/
				 String query="select s.student_id "+
					 		   "from student s, tramstop t "+ 
					 		   "WHERE SDO_RELATE(s.shape,sdo_geometry(2003,NULL,NULL, "+
	                             "sdo_elem_info_array(1,1003,4), "+
	                             "sdo_ordinate_array(t.shape.sdo_point.x+t.tram_radius,t.shape.sdo_point.y, t.shape.sdo_point.x,t.shape.sdo_point.y+t.tram_radius, "+
	                             "t.shape.sdo_point.x-t.tram_radius,t.shape.sdo_point.y "+
	                             ")),'mask=ANYINTERACT') = 'TRUE' and t.tramstation_id = 't2ohe' "+
	                             "intersect "+
								"select s.student_id "+ 
								"from student s, tramstop t "+ 
								"WHERE SDO_RELATE(s.shape,sdo_geometry(2003,NULL,NULL, "+
                             "sdo_elem_info_array(1,1003,4), "+
                             "sdo_ordinate_array(t.shape.sdo_point.x+t.tram_radius,t.shape.sdo_point.y, t.shape.sdo_point.x,t.shape.sdo_point.y+t.tram_radius, "+
                             "t.shape.sdo_point.x-t.tram_radius,t.shape.sdo_point.y "+
                             ")),'mask=ANYINTERACT') = 'TRUE' and t.tramstation_id = 't6ssl' "+
                             "union "+
                             
									"select b.building_id "+ 
									"from building b, tramstop t "+ 
									"WHERE SDO_RELATE(b.shape,sdo_geometry(2003,NULL,NULL, "+
                             "sdo_elem_info_array(1,1003,4), "+
                             "sdo_ordinate_array(t.shape.sdo_point.x+t.tram_radius,t.shape.sdo_point.y, t.shape.sdo_point.x,t.shape.sdo_point.y+t.tram_radius, "+
                             "t.shape.sdo_point.x-t.tram_radius,t.shape.sdo_point.y)),'mask=ANYINTERACT') = 'TRUE' and t.tramstation_id = 't2ohe' "+
                             "intersect "+
							"select b.building_id "+ 
							"from building b, tramstop t "+ 
							"WHERE SDO_RELATE(b.shape,sdo_geometry(2003,NULL,NULL, "+
                            "sdo_elem_info_array(1,1003,4), "+
                             "sdo_ordinate_array(t.shape.sdo_point.x+t.tram_radius,t.shape.sdo_point.y, t.shape.sdo_point.x,t.shape.sdo_point.y+t.tram_radius, "+
                             "t.shape.sdo_point.x-t.tram_radius,t.shape.sdo_point.y)),'mask=ANYINTERACT') = 'TRUE' and t.tramstation_id = 't6ssl'";
				 //System.out.println(query); Can use Sdo_filter also
				 try {
						
						pstatement = connection.prepareStatement(query);
						rSet = pstatement.executeQuery();
						String student = null;
						while(rSet.next()){
							student = rSet.getString(1);
							System.out.println(student);
					
						}
					} catch (SQLException e) {
						
					}finally{
						pstatement.close();
						connection.close();
					}
			}
			if(args[1].equalsIgnoreCase("2")){
				 String query = "select s.student_id,t.tramstation_id"+ 
				 " from tramstop t, student s"+
				 " where sdo_nn(t.shape,s.shape,'sdo_num_res=2') = 'TRUE' and s.student_id in ("+
				 " select student_id from student )";
				 System.out.println(query);
				 String tram = null;
				 try {
						
						pstatement = connection.prepareStatement(query);
						rSet = pstatement.executeQuery();
						String student = null;
						while(rSet.next()){
							student = rSet.getString(1);
							tram = rSet.getString(2);
							System.out.println(student);
							System.out.println(tram);
						}
					} catch (SQLException e) {
						
					}finally{
						pstatement.close();
						connection.close();
					}
			}
			if(args[1].equalsIgnoreCase("3")){
				 String query =  "select * from"+
				 "(SELECT t.TRAMSTATION_ID,count(t.tramstation_id)  FROM TRAMSTOP t,building b WHERE SDO_WITHIN_DISTANCE(t.shape,b.shape,"+
				  "'distance=250') = 'TRUE' and b.building_id in (select building_id from building)"+ 
				  "group by t.tramstation_id order by count(t.tramstation_id) desc )  where rownum <2";
				 try {
						
						pstatement = connection.prepareStatement(query);
						rSet = pstatement.executeQuery();
						String student = null;
						while(rSet.next()){
							student = rSet.getString(1);
							System.out.println(student);
						}
					} catch (SQLException e) {
						
					}finally{
						pstatement.close();
						connection.close();
					}
			}
			if(args[1].equalsIgnoreCase("4")){
				 String query = "select * from ("+
						 " select s.student_id ,count(s.student_id)"+
						 " from student s, building b"+
						 " where sdo_nn(s.shape,b.shape,'sdo_num_res=1') = 'TRUE' and b.building_id in ("+
						 " select building_id from building"+
						 " ) group by s.student_id order by count(s.student_id) desc)"+  
						 " where rownum<6 ";
				 int count = 0;
				 try {
						
						pstatement = connection.prepareStatement(query);
						rSet = pstatement.executeQuery();
						String student = null;
						while(rSet.next()){
							student = rSet.getString(1);
							count = rSet.getInt(2);
							System.out.println(student+"\t"+ count);
						}
					} catch (SQLException e) {
						
					}finally{
						pstatement.close();
						connection.close();
					}
			}
			if(args[1].equalsIgnoreCase("5")){
				 String query = " SELECT MIN(lower_x)AS lower_left_x,"+
								  " MIN (lower_y)    AS lower_left_y ,"+
								  " MAX(upper_x)     AS upper_right_x,"+
								  " MAX(upper_y)     AS upper_right_y"+
								" FROM"+
								  "(select"+
								   " SDO_GEOM.SDO_MIN_MBR_ORDINATE(BUILDING.shape, M.DIMINFO,1) AS LOWER_X,"+
								     " SDO_GEOM.SDO_MIN_MBR_ORDINATE(BUILDING.shape, M.DIMINFO,2) AS LOWER_Y,"+
								     " SDO_GEOM.SDO_MAX_MBR_ORDINATE(BUILDING.shape, M.DIMINFO,1) AS UPPER_X,"+
								    " SDO_GEOM.SDO_MAX_MBR_ORDINATE(BUILDING.shape, M.DIMINFO,2) AS UPPER_Y"+
								  " FROM BUILDING ,"+
								    " USER_SDO_GEOM_METADATA M"+
								  " WHERE M.TABLE_NAME = 'BUILDING'"+
								 " AND M.COLUMN_NAME  = 'SHAPE'"+
								 " AND BUILDING.BUILDING_NAME LIKE upper('SS%')"+
								 " )";
				 try {
						
						pstatement = connection.prepareStatement(query);
						rSet = pstatement.executeQuery();
						int lower_x = 0;
						int lower_y = 0;
						int upper_x = 0;
						int upper_y = 0;
						while(rSet.next()){
							lower_x = rSet.getInt(1);
							lower_y = rSet.getInt(2);
							upper_x = rSet.getInt(3);
							upper_y = rSet.getInt(4);
							System.out.println("lower_x : " +lower_x +" lower_y :"+lower_y +" upper_x :"+upper_x +" upper_y :"+ upper_y);
						}
					} catch (SQLException e) {
						
					}finally{
						pstatement.close();
						connection.close();
					}
			}
			
		}
			
		//********************Query 4*****************************************End
}
}
