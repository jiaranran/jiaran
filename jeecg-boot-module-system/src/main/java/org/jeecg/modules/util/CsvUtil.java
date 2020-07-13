package org.jeecg.modules.util;

import org.apache.commons.io.FileUtils;
import org.jeecg.modules.predict.entity.ScmNretailIpw;
import org.jeecg.modules.stkpredict.entity.ScmNretailIpd;
import org.jeecg.modules.stkscm.vo.ScmNretailpiVo;
import org.jeecg.modules.time.entity.ScmNretailTime;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Desc 导出csv线上版本
 * @Date 2019/2/18 16:10
 * @Author cui_yl
 */
public class CsvUtil {


    public static ArrayList<String> exportCsv(List<ScmNretailIpw> scmNretailIpds, List<ScmNretailpiVo> scmNretailpiVos, String writePath) throws IOException {
        ArrayList<String> readfileNames = new ArrayList<>();
        for (ScmNretailpiVo vo:scmNretailpiVos ) {
            String matnr = vo.getMatnr();
            String prov = vo.getProv();
            File dir = new File(writePath+prov);
            if(!dir.exists()){
                dir.mkdir();
            }
            String fileName="scm_nretail_ipw_"+matnr+"_"+prov+".csv";
            readfileNames.add(fileName);
            File file = new File(writePath+prov+File.separator+fileName);

            //构建输出流，同时指定编码
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
            //写内容
            for(ScmNretailIpw scmNretailIpd:scmNretailIpds){
                if(scmNretailIpd.getMatnr().equals(matnr)&&scmNretailIpd.getProv().equals(prov)){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String ds = format.format(scmNretailIpd.getDs());
                    ow.write(scmNretailIpd.getMatnr());
                    ow.write(",");
                    ow.write(scmNretailIpd.getProv());
                    ow.write(",");
                    ow.write(ds);
                    ow.write(",");
                    ow.write(String.valueOf(scmNretailIpd.getCnt()));
                    //写完一行换行
                    ow.write("\r\n");
                }
            }
            ow.flush();
            ow.close();
        }
        return readfileNames;
    }
    public static ArrayList<String> exportCsv2(List<ScmNretailIpw> scmNretailIpds, String writePath) throws IOException {
        OutputStreamWriter ow = null;
        ArrayList<String> readfileNames = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String matnr=scmNretailIpds.get(0).getMatnr() ;
        String prov=scmNretailIpds.get(0).getProv();
        String fileName="scm_nretail_ipw_"+matnr+"_"+prov+".csv";
        readfileNames.add(fileName);
        File dir = new File(writePath+prov);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(writePath+prov+File.separator+fileName);
        ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
        for (ScmNretailIpw ipw:scmNretailIpds ) {
            if(!ipw.getMatnr().equals(matnr)||!ipw.getProv().equals(prov)){
                //构建输出流，同时指定编码
                ow.flush();
                ow.close();
                matnr=ipw.getMatnr() ;
                prov=ipw.getProv();
                fileName="scm_nretail_ipw_"+matnr+"_"+prov+".csv";
                readfileNames.add(fileName);
                dir = new File(writePath+prov);
                if(!dir.exists()){
                    dir.mkdir();
                }
                file = new File(writePath+prov+File.separator+fileName);
                ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
            }
            //写内容
            String ds = format.format(ipw.getDs());
            ow.write(ipw.getMatnr());
            ow.write(",");
            ow.write(ipw.getProv());
            ow.write(",");
            ow.write(ds);
            ow.write(",");
            ow.write(String.valueOf(ipw.getCnt()));
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();
        return readfileNames;
    }
    public static ArrayList<String> exportCsv3(List<ScmNretailIpd> scmNretailIpds, String writePath) throws IOException {
        OutputStreamWriter ow = null;
        ArrayList<String> readfileNames = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String matnr=scmNretailIpds.get(0).getMatnr() ;
        String prov=scmNretailIpds.get(0).getProv();
        String fileName="scm_nretail_ipw_"+matnr+"_"+prov+".csv";
        readfileNames.add(fileName);
        File dir = new File(writePath+prov);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(writePath+prov+File.separator+fileName);
        ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
        for (ScmNretailIpd ipw:scmNretailIpds ) {
            if(!ipw.getMatnr().equals(matnr)||!ipw.getProv().equals(prov)){
                //构建输出流，同时指定编码
                ow.flush();
                ow.close();
                matnr=ipw.getMatnr() ;
                prov=ipw.getProv();
                fileName="scm_nretail_ipw_"+matnr+"_"+prov+".csv";
                readfileNames.add(fileName);
                dir = new File(writePath+prov);
                if(!dir.exists()){
                    dir.mkdir();
                }
                file = new File(writePath+prov+File.separator+fileName);
                ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
            }
            //写内容
            String ds = format.format(ipw.getDs());
            ow.write(ipw.getMatnr());
            ow.write(",");
            ow.write(ipw.getProv());
            ow.write(",");
            ow.write(ds);
            ow.write(",");
            ow.write(String.valueOf(ipw.getCnt()));
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();
        return readfileNames;
    }
    public static ArrayList<String> exportTimeCsv(List<ScmNretailTime> scmTimes, String writePath) throws IOException {
        OutputStreamWriter ow = null;
        ArrayList<String> readfileNames = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String matnr=scmTimes.get(0).getMatnr() ;
        String prov=scmTimes.get(0).getProv();
        String fileName="scm_nretail_time_"+matnr+"_"+prov+".csv";
        readfileNames.add(fileName);
        File dir = new File(writePath+prov);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(writePath+prov+File.separator+fileName);
        ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
        for (ScmNretailTime ipd:scmTimes ) {
            if(!ipd.getMatnr().equals(matnr)||!ipd.getProv().equals(prov)){
                //构建输出流，同时指定编码
                ow.flush();
                ow.close();
//                FileUtils.copyFile(file, destFile);
                matnr=ipd.getMatnr() ;
                prov=ipd.getProv();
                fileName="scm_nretail_time_"+matnr+"_"+prov+".csv";
                readfileNames.add(fileName);
                dir = new File(writePath+prov);
                if(!dir.exists()){
                    dir.mkdir();
                }
                file = new File(writePath+prov+File.separator+fileName);
                ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
            }
            //写内容
            String orDate = format.format(ipd.getOrderCreateDate());
            ow.write(ipd.getMatnr());
            ow.write(",");
            ow.write(ipd.getProv());
            ow.write(",");
            ow.write(orDate);
            ow.write(",");
            ow.write(format.format(ipd.getRealSendDate()));
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();

        return readfileNames;
    }


    public static void readCsv(String inpath) {

//            List<> list = new ArrayList<>(); // 保存读取到的CSV数据
        inpath="D:\\Desktop\\office\\result\\predict_result.csv";
        try {

            File file = new File(inpath); // 判断文件是否存在

            if (!file.exists()) {

                System.out.println("文件不存在！");

            } else {

                System.out.println("文件存在！");

                BufferedReader reader = new BufferedReader(new FileReader(inpath)); // 读取CSV文件

                String line = null;// 循环读取每行

                while ((line = reader.readLine()) != null) {

                    String[] row = line.split(",", -1); // 分隔字符串（这里用到转义），存储到List<taskRule>里
                    String arr = Arrays.toString(row);
                    System.out.println(arr);
                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    /**

     * 读取csv文件用list对象存储的公共调用方法

     * @param inpath csv文件存储路径

     * @param obj 和csv文件对应的实体类

     * @return 返回List<Object>对象

     */

    public void readCsv2(String inpath, Object obj) {

        List<Object> list = new ArrayList<Object>(); // 保存读取到的CSV数据

        try {

            File file = new File(inpath); // 判断文件是否存在

            if (!file.exists()) {

                System.out.println("文件不存在！");

            } else {

                System.out.println("文件存在！");

                BufferedReader reader = new BufferedReader(new FileReader(inpath)); // 读取CSV文件

                String line = null; // 循环读取每行

                while ((line = reader.readLine()) != null) {

                    String[] row = line.split("\\|", -1); // 分隔字符串（这里用到转义），存储到List<Object>里

                    Class clazz = obj.getClass(); // 通过反射获取运行时类

                    Object infos = clazz.newInstance(); // 创建运行时类的对象

                    Field[] fs = infos.getClass().getDeclaredFields(); // 得到类中的所有属性集合

                    for (int i = 0; i < fs.length; i++) {

                        Field f = fs[i];

                        f.setAccessible(true); // 设置这些属性值是可以访问的

                        String type = f.getType().toString(); // 得到此属性的类型

                        if (type.endsWith("String")) {

                            f.set(infos, row[i]); // 给属性赋值

                        }

                    }

                    list.add(infos);

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }



    }



    public static void main(String[] args) throws IOException { //调用测试
        File file = new File("D:\\\\Desktop\\\\office\\\\out\\\\4400\\scm_nretail_time_002127096701003000_4400.csv");
        File destFile = new File("D:\\\\Desktop\\\\office\\\\out\\\\4400\\scm_nretail_time_X02127096701003000_4400.csv");
        FileUtils.copyFile(file, destFile);
    }

}


