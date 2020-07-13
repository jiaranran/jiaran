package org.jeecg.modules.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
public class ResouceUtil {

    @Test
    public Map<String,String> getResource(ResourceLoader resourceLoader, String path)  {
        Map<String,String> map = new HashMap<String,String>();
        try {
            Resource resource = resourceLoader.getResource(path);
            InputStream is = null;

            is = resource.getInputStream();

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String data = null;
            while((data = br.readLine()) != null) {
                String[] split = data.split("=");
                map.put(split[0],split[1]);
//                if(map.size()==5) break;
            }
            System.out.println(map);
            br.close();
            isr.close();
            is.close();
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


}
