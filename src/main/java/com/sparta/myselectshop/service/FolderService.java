package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.FolderResponseDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class FolderService {
    private final FolderRepository folderRepository;
    // 로그인한 회원에 폴더들 등록
    public void addFolders(List<String> folderNames, User user) {
        // 입력으로 들어온 폴더 이름을 기준으로, 회원이 이미 생성한 폴더들을 조회합니다.
        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user, folderNames);
        
        List<Folder> folderList = new ArrayList<>();

        for (String folderName : folderNames) {
            // 이미 생성한 폴더가 아닌 경우만 폴더 생성
            if(!isExistFolderName(folderName, existFolderList)) {// 중복하는 일치하는 폴더가 없는 경우!
                Folder folder = new Folder(folderName, user); //새롭게 폴더 생성
                folderList.add(folder);
            }else{
                throw new IllegalArgumentException("폴더명이 중복되었습니다."); // 하나라도 중복되는 경우
            }
        }
        folderRepository.saveAll(folderList);
    }
    // 로그인한 회원이 등록된 모든 폴더 조회
    public List<FolderResponseDto> getFolders(User user) {    //폴더정보 ResponseDto에 담아서 반환할것임.
        List<Folder> folderList = folderRepository.findAllByUser(user);
        List<FolderResponseDto> responseDtoList = new ArrayList<>();

        for (Folder folder : folderList) {
            responseDtoList.add(new FolderResponseDto(folder));
        }

        return responseDtoList;
    }

    // 기존폴더네임과 새로받은 폴더네임이 중복되는지 체크하는 메소드
    private boolean isExistFolderName(String folderName, List<Folder> existFolderList) {
        // 기존 폴더 리스트에서 folder name 이 있는지?
        for (Folder existFolder : existFolderList) {
            if(folderName.equals(existFolder.getName())) { //일치하는것이 있다면
                return true;
            }
        }
        return false; //일치하는것이 없다면
    }

}
