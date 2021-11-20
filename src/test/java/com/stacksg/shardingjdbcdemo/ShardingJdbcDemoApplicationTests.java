package com.stacksg.shardingjdbcdemo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stacksg.shardingjdbcdemo.entity.Course;
import com.stacksg.shardingjdbcdemo.mapper.CourseMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShardingJdbcDemoApplicationTests {

    @Autowired
    private CourseMapper courseMapper;

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
}
