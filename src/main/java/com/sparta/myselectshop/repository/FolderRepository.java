package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findAllByUserAndNameIn(User user, List<String> folderNames);// 이름 여러개 쿼리문생각.
    // findAllByUserAndNameIn 의 쿼리문 생각 -> select * from folder where user_id = ? and name in ('1', '2', '3', ...)
    // 이처럼 쿼리메소드가 실제 DB의 쿼리문이 어떻게 작동할지 예상하는버릇들이기

    List<Folder> findAllByUser(User user);

}
