package com.ustc.quincy.docshare.util;


import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import java.io.FileInputStream;
import java.io.InputStream;


import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;



/**
 * Author: Created by QuincyHu on 2016/6/28 0028 10:44.
 * Email:  zhihuqunxing@163.com
 */
public class JDBCUtils {
    private static String driver;
    private static String url;
    private static String user;
    private static String password;


    //1 获得连接
    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.v("DocShare","注册驱动失败！");
        }
        try {
            //2 获得连接
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.60.70:3306/user?user=root&password=admin");
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("DocShare","创建连接失败!");
        }
        if (conn != null)
            Log.v("DocShare","创建连接成功！");
        return conn;
    }

    //2 释放资源
    //1> 参数可能为空
    //2> 调用close方法要抛出异常,确保即使出现异常也能继续关闭
    //3>关闭顺序,需要从小到大
    public  static void  close(Connection conn , Statement st , ResultSet rs){

        try {
            if(rs!=null){
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                if(st!=null){
                    st.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                try {
                    if(conn!=null){
                        conn.close();
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }
}
