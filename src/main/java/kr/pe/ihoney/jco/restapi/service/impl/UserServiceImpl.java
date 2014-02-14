package kr.pe.ihoney.jco.restapi.service.impl;

import kr.pe.ihoney.jco.restapi.common.exception.RestApiException;
import kr.pe.ihoney.jco.restapi.domain.User;
import kr.pe.ihoney.jco.restapi.repository.UserRepository;
import kr.pe.ihoney.jco.restapi.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: ihoneymon
 * Date: 14. 2. 2
 * Time: 오후 12:52
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly=false, rollbackFor=Exception.class)
    @Cacheable(value="userCache", key="#user.email.concat('.User')")
    public User save(User user) throws RestApiException {
        User existUser = userRepository.findByEmail(user.getName());
        if(existUser != null) {
            throw new RestApiException("user.exception.exist-user");
        }
        log.debug(">> Save user: {}", user);
        return userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional(readOnly=false, rollbackFor=Exception.class)
    @CacheEvict(value="userCache", key="#user.email.concate('.User')")
    public void delete(User user) throws RestApiException {
        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch(Exception e) {
            throw new RestApiException(e);
        }
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
