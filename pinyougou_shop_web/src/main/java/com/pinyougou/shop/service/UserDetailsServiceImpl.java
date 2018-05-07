package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.shop.service
 * @date 2018/4/26/0026
 */
public class UserDetailsServiceImpl implements UserDetailsService{
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("从页面传过来的用户名"+username);
        TbSeller tbSeller = sellerService.findOne(username);
        if(tbSeller==null){
            return null;
        }
        if(!"1".equals(tbSeller.getStatus())){
            return  null;
        }

        System.out.println(tbSeller.getPassword());
        return new User(username,tbSeller.getPassword(), AuthorityUtils.createAuthorityList("ROLE_SELLER"));
    }
}
