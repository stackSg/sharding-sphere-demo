package com.stacksg.shardingjdbcdemo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stacksg.shardingjdbcdemo.entity.Course;
import com.stacksg.shardingjdbcdemo.entity.Udict;
import com.stacksg.shardingjdbcdemo.entity.User;
import com.stacksg.shardingjdbcdemo.mapper.CourseMapper;
import com.stacksg.shardingjdbcdemo.mapper.UdictMapper;
import com.stacksg.shardingjdbcdemo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShardingJdbcDemoApplicationTests {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UdictMapper udictMapper;

    /*水平分表测试*/
    /**
     * 插入
     */
    @Test
    void addCourse() {
        for (int i = 0; i < 10; i++) {
            Course course = new Course();
            course.setCname("java" + i);
            course.setUserId(100l);
            course.setCstatus("normal" + i);
            courseMapper.insert(course);
        }
    }

    /**
     * 查询
     */
    @Test
    void select(){
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", 668927137868152832l);
        Course course = courseMapper.selectOne(queryWrapper);
        System.out.println(course);
    }


    /*水平分库测试*/

    @Test
    void addCourseDB(){
        for (int i = 0; i < 10; i++) {
            Course course = new Course();
            course.setCname("java" + i);
            //根据user_id分库
            course.setUserId((long) i);
            course.setCstatus("normal" + i);
            courseMapper.insert(course);
        }
    }

    @Test
    void findCourseDB(){
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", 668939709027713024l);
        queryWrapper.eq("user_id", 9);
        System.out.println(courseMapper.selectOne(queryWrapper));
    }

    /*垂直分库测试*/
    @Test
    void addUserDB(){
        User user = new User();
        user.setUsername("curry");
        user.setUstatus("sss");
        userMapper.insert(user);
    }

    @Test
    void findUserDB(){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", 668959280048635905l);
        System.out.println(userMapper.selectOne(queryWrapper));
    }

    /*公共表测试*/

    @Test
    void addUdict(){
        Udict udict = new Udict();
        udict.setUstatus("sss");
        udict.setUvalue("满级");
        udictMapper.insert(udict);
    }

    @Test
    void delUdict(){
        QueryWrapper<Udict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dictid", 669122603473960961l);
        System.out.println(udictMapper.delete(queryWrapper));
    }
}
