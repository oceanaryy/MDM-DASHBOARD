package com.board.service;

import java.util.Collections;
import java.util.List;

import com.board.domain.DeviceVersionDTO;
import com.board.domain.VersionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.board.domain.BoardDTO;
import com.board.mapper.BoardMapper;
import com.board.paging.PaginationInfo;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper boardMapper;

	@Override
	public boolean update_chk(BoardDTO params) {
		int queryResult = 0;
		queryResult = boardMapper.update_chk(params);
		/*업데이트 할 항목이 있으면 0, 없으면 1*/
		return (queryResult == 0) ? true : false;
	}

	@Override
	public boolean update_chk_for_api(BoardDTO params) {
		int queryResult = 0;
		queryResult = boardMapper.update_chk_for_api(params);
		/*업데이트 할 항목이 있으면 0, 없으면 1*/
		return (queryResult > 0) ? true : false;
	}

	@Override
	public boolean update_notify(BoardDTO params) {
		return boardMapper.updatenotify(params);
	}

	@Override
	public String uptAble(BoardDTO params) {
		return boardMapper.uptAble(params);
	}

	@Override
	public BoardDTO getBoardDetail(Long idx) {
		return boardMapper.selectBoardDetail(idx);
	}

	@Override
	public boolean deleteBoard(Long idx) {
		int queryResult = 0;

		BoardDTO board = boardMapper.selectBoardDetail(idx);

		if (board != null && "N".equals(board.getDeleteYn())) {
			queryResult = boardMapper.deleteBoard(idx);
		}

		return (queryResult == 1) ? true : false;
	}
	/*디바이스 리스트 가져오는 함수*/
	@Override
	public List<BoardDTO> getDeviceList(BoardDTO params) {
		List<BoardDTO> boardList = Collections.emptyList();

		int boardTotalCount = boardMapper.selectBoardTotalCount(params);

		PaginationInfo paginationInfo = new PaginationInfo(params);
		paginationInfo.setTotalRecordCount(boardTotalCount);

		params.setPaginationInfo(paginationInfo);

		if (boardTotalCount > 0) {
			boardList = boardMapper.selectDeviceList(params);
		}

		return boardList;
	}
	/*버전 가져오는 함수*/
	@Override
	public List<VersionDTO> getVerionList() {
		List<VersionDTO> versionList = Collections.emptyList();
		versionList = boardMapper.selectVersion();
		return versionList;
	}

	@Override
	public List<DeviceVersionDTO> getDeviceVerion() {
		List<DeviceVersionDTO> versionList = Collections.emptyList();
		versionList = boardMapper.selectVersionCount();
		return versionList;
	}

	@Override
	public boolean uptReserv(VersionDTO params) {
		int queryResult = 0;
		queryResult = boardMapper.uptReserv(params);
		return (queryResult > 0 ) ? true : false;
	}

	@Override
	public VersionDTO selectLatestVersion(BoardDTO params) {
		return boardMapper.selectLatestVersion(params);
	}

	@Override
	public String preUpdateAble(BoardDTO params) {
		return boardMapper.preUpdateAble(params);
	}

}
