package com.etoak.service.impl;

import com.etoak.bean.User;
import com.etoak.mapper.UserMapper;
import com.etoak.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    JmsTemplate jmsTemplate;

    @Override
    public int addUser(User user) {
        // 密码加密
        String password = user.getPassword();
        password = DigestUtils.md5Hex(password);
        user.setPassword(password);
        int addResult = userMapper.addUser(user);
        // 把自增id返回到user对象
        log.info("User.id - {}",user.getId());
        // 发送jms消息
        /*jmsTemplate.send("email",session -> {
            Email email = new Email();
            email.setSubject("用户激活邮件");
            email.setReceiver(user.getEmail());
            email.setContent("请点击激活:http://localhose:8000/boot/user/active" + user.getId());
            return session.createTextMessage(JSONObject.toJSONString(email));
        });*/
        return addResult;
    }

    @Override
    public User queryByName(String name) {
        return userMapper.queryByName(name);
    }
}
