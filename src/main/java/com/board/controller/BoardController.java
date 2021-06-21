package com.board.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.board.domain.DeviceVersionDTO;
import com.board.domain.VersionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import com.board.constant.Method;
import com.board.domain.BoardDTO;
import com.board.service.BoardService;
import com.board.util.UiUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BoardController extends UiUtils {

    @Autowired
    private BoardService boardService;

    @GetMapping(value = "/")
    public String redirect(@ModelAttribute("params") BoardDTO params, Model model) {
        return openBoardList(params, model);
    }

    @GetMapping(value = "/device/update_chk")
    public String openBoardWrite(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            model.addAttribute("board", new BoardDTO());
        } else {
            BoardDTO board = boardService.getBoardDetail(idx);
            if (board == null || "Y".equals(board.getDeleteYn())) {
                return showMessageWithRedirect("없는 게시글이거나 이미 삭제된 데이터입니다.", "/device/list", Method.GET, null, model);
            }
            model.addAttribute("board", board);
        }
        return "device/write";
    }


    @GetMapping(value = "/device/reservation")
    public String updateReservationPage(@ModelAttribute("params") VersionDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            model.addAttribute("Version", new VersionDTO());
        } else {
            model.addAttribute("Version", new VersionDTO());
        }
        return "device/reservation";
    }

    @PostMapping(value = "/device/updateReservation")
    public String updateReservation(@ModelAttribute("params") final VersionDTO params, Model model) {
        Map<String, Object> pagingParams = getPagingParams(params);
        try {
            boolean isRegistered = boardService.uptReserv(params);

        } catch (DataAccessException e) {
            e.printStackTrace();
            return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/device/list", Method.GET, pagingParams, model);

        } catch (Exception e) {
            e.printStackTrace();
            return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/device/list", Method.GET, pagingParams, model);
        }
        return showMessageWithRedirect("등록이 완료되었습니다.", "/device/list", Method.GET, pagingParams, model);
    }

    @PostMapping(value = "/device/update")
    public String registerBoard(@ModelAttribute("params") final BoardDTO params, Model model) {
        Map<String, Object> pagingParams = getPagingParams(params);
        try {
            boolean isRegistered = boardService.update_chk(params);
            if (isRegistered == false) {
                return showMessageWithRedirect("값 변경에 실패하였습니다.", "/device/list", Method.GET, pagingParams, model);
            }
            String uptAble = boardService.uptAble(params);
            if (uptAble.equals("")) {
                return showMessageWithRedirect("업데이트 할 항목이 있습니다", "/device/list", Method.GET, pagingParams, model);
            }
        } catch (DataAccessException e) {
            return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/device/list", Method.GET, pagingParams, model);

        } catch (Exception e) {
            e.printStackTrace();
            return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/device/list", Method.GET, pagingParams, model);
        }
        return showMessageWithRedirect("등록이 완료되었습니다.", "/device/list", Method.GET, pagingParams, model);
    }

    /*실제 API 요청*/
    @PostMapping(value = "/device/updateChk2")
    @ResponseBody
    public String device_update_check2(@ModelAttribute("params") final BoardDTO params, Model model) {
        return "200";
    }

    /*실제 API 요청*/
    @PostMapping(value = "/device/updateChk")
    @ResponseBody
    public Map<String, String> device_update_check(@ModelAttribute("params") final BoardDTO params, Model model) {
        Map<String, Object> pagingParams = getPagingParams(params);
        Map<String, String> returnMaps = new HashMap<>();
        try {
            /*시간 체크*/
            boolean update = boardService.update_chk_for_api(params);
            if (!update) {
                returnMaps.put("resultCode", "9");
                return returnMaps;
            }

            String preUpdate = boardService.preUpdateAble(params);
            if(preUpdate == null || preUpdate.equals("")){
                returnMaps.put("resultCode", "9");
                return returnMaps;
            } else if(preUpdate.equals("OFF")){
                returnMaps.put("resultCode", "0");
                return returnMaps;
            }

            /*실제 업데이트 할 항목이 있는지 체크*/
            String uptAble = boardService.uptAble(params);
            if (uptAble == null || uptAble.equals("")) {
                /*업데이트 할 항목이 있으므로 리턴*/
                VersionDTO v = boardService.selectLatestVersion(params);
                if(v != null) {
                    boardService.update_notify(params);
                    if(v.getUpdateDelay() == null){
                        returnMaps.put("resultCode", "9");
                        return returnMaps;
                    }
                    returnMaps.put("appPath", v.getAppPath());
                    returnMaps.put("insertTime", v.getInsertTime());
                    returnMaps.put("versionName", v.getVersionName());
                    returnMaps.put("updateDelay", v.getUpdateDelay());
                    returnMaps.put("resultCode", "1");
                }else{
                    returnMaps.put("resultCode", "0");
                    return returnMaps;
                }
                return returnMaps;
            }
        } catch (DataAccessException e) {
            returnMaps.put("resultCode", "9");
            e.printStackTrace();
            return returnMaps;
        } catch (Exception e) {
            returnMaps.put("resultCode", "9");
            e.printStackTrace();
            return returnMaps;
        }
        returnMaps.put("resultCode", "0");
        return returnMaps;
    }
    @PostMapping(value = "/test")
    @ResponseBody
    public Map<String, String> cold(@ModelAttribute("params") final BoardDTO params, Model model) {
        Map<String, String> returnMaps = new HashMap<>();
        returnMaps.put("value","ok");
        return returnMaps;
    }
    /*메인 페이지 조회*/
    @GetMapping(value = "/device/list")
    public String openBoardList(@ModelAttribute("params") BoardDTO params, Model model) {
        List<BoardDTO> DeviceList = boardService.getDeviceList(params);
        model.addAttribute("deviceList", DeviceList);

        List<VersionDTO> VersionList = boardService.getVerionList();
        model.addAttribute("versionList", VersionList);

        List<DeviceVersionDTO> DeviceVersion = boardService.getDeviceVerion();
        model.addAttribute("deviceVersion", DeviceVersion);
        return "device/list";
    }

    @GetMapping(value = "/board/view.do")
    public String openBoardDetail(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            return showMessageWithRedirect("올바르지 않은 접근입니다.", "/device/list", Method.GET, null, model);
        }

        BoardDTO board = boardService.getBoardDetail(idx);
        if (board == null || "Y".equals(board.getDeleteYn())) {
            return showMessageWithRedirect("없는 게시글이거나 이미 삭제된 게시글입니다.", "/device/list", Method.GET, null, model);
        }
        model.addAttribute("board", board);

        return "board/view";
    }

    @PostMapping(value = "/board/delete.do")
    public String deleteBoard(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            return showMessageWithRedirect("올바르지 않은 접근입니다.", "/device/list", Method.GET, null, model);
        }

        Map<String, Object> pagingParams = getPagingParams(params);
        try {
            boolean isDeleted = boardService.deleteBoard(idx);
            if (isDeleted == false) {
                return showMessageWithRedirect("게시글 삭제에 실패하였습니다.", "/device/list", Method.GET, pagingParams, model);
            }
        } catch (DataAccessException e) {
            return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/device/list", Method.GET, pagingParams, model);

        } catch (Exception e) {
            return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/device/list", Method.GET, pagingParams, model);
        }

        return showMessageWithRedirect("게시글 삭제가 완료되었습니다.", "/device/list", Method.GET, pagingParams, model);
    }

    @GetMapping("/update/download/{detail}")
    public void download(@RequestParam Map<String, Object> map, HttpServletResponse response, @PathVariable("detail") String v) throws IOException {
        Map<String, Object> resultMap = map;
        byte fileByte[] = org.apache.commons.io.FileUtils.readFileToByteArray(new File("D:\\SOFaceAndroidUpdateServer\\updateFiles_dev\\" + v + ".apk"));
        response.setContentType("application/octet-stream");
        response.setContentLength(fileByte.length);
        response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode("app.apk", "UTF-8") + "\";");
        response.getOutputStream().write(fileByte);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    /*실제 API 요청*/
    @PostMapping(value = "/device/first")
    @ResponseBody
    public Map<String, String> device_first() {
        Map<String, String> returnMaps = new HashMap<>();
        try {
            /*업데이트 할 항목이 있으므로 리턴*/
            BoardDTO params = new BoardDTO();
            params.setDeviceId("");
            VersionDTO v = boardService.selectLatestVersion(params);
            returnMaps.put("appPath", v.getAppPath());
            returnMaps.put("insertTime", v.getInsertTime());
            returnMaps.put("versionName", v.getVersionName());
            returnMaps.put("resultCode", "1");
            return returnMaps;
        } catch (DataAccessException e) {
            returnMaps.put("resultCode", "9");
            e.printStackTrace();
            return returnMaps;
        } catch (Exception e) {
            returnMaps.put("resultCode", "9");
            e.printStackTrace();
            return returnMaps;
        }
    }

}
