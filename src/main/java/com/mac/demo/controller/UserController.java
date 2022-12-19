package com.mac.demo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mac.demo.dto.Board;
import com.mac.demo.dto.User;
import com.mac.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userSvc;

//	계정추가폼
	@GetMapping("/addForm")
	public ModelAndView addForm() {

		ModelAndView mav = new ModelAndView("thymeleaf/mac/User/addForm");

		mav.addObject("user", new User());

		return mav;
	}
	
//	아이디 체크후 추가폼
	@PostMapping("/addForm/{idMac}")
	public ModelAndView addForm2(@PathVariable("idMac")String idMac) {

		ModelAndView mav = new ModelAndView("thymeleaf/mac/User/addForm");

		User user = User.builder()
				.user_id(idMac)
				.build();

		mav.addObject("user", user);

		return mav;
	}
	
//	아이디 체크, 이메일 인증후 추가폼
	@PostMapping("/addForm/{idMac}/{emailMac}")
	public ModelAndView addForm3(@PathVariable("idMac")String idMac,
								 @PathVariable("emailMac")String emailMac,
								 @RequestParam(name ="check", defaultValue="0")int check, Model model) {

		ModelAndView mav = new ModelAndView("thymeleaf/mac/home/home");

		User user = User.builder()
				.user_id(idMac)
				.email(emailMac)
				.build();

		if (check == 1) {
			mav.setViewName("thymeleaf/mac/User/addForm");
			mav.addObject("user", user);
			return mav;
		}

		return mav;
	}
	
//	계정추가
	@PostMapping("/add")
	public Map<String,Object> add(User user) {
		Map<String, Object> map = new HashMap<>();
		map.put("result", userSvc.add(user));
		return map;
	}
	
//	계정리스트
	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("list", userSvc.getList());
		return "thymeleaf/mac/User/userlist";
	}
	
//	마이페이지
	@GetMapping("/detail/{idMac}")
	public String mypage(@PathVariable("idMac")String idMac,
						 Model model,
						 HttpSession session,
						 @RequestParam(name="page", required = false,defaultValue = "1") int page) {
		User user = userSvc.getOne(idMac);
		if(idMac.equals((String)session.getAttribute("idMac"))==false) return "redirect:/home";

		if((String)session.getAttribute("idMac") == null) return "thymeleaf/mac/home/home";

		model.addAttribute("user", user);
		
		PageHelper.startPage(page, 5);
		PageInfo<Board> pageInfo = new PageInfo<>(userSvc.findWrite(idMac));
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("page", page);
		
		return "thymeleaf/mac/User/myPage";

	}
	
//	계정 삭제
	@DeleteMapping("/deleted")
	public Map<String,Object> deleted(com.mac.demo.model.User user, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Long user_idx = user.getNummac();
		boolean result = userSvc.delete(user_idx);
		map.put("result", result);
		
		if((String)session.getAttribute("idMac") == null){ //세션을 가져옴
			result = false;
			map.put("result", result);
			return map;
		};
		if(result==true) {
			session.removeAttribute("idMac");
		};
		
		return map;
	}
	
//  유저 업데이트폼
	@GetMapping("/updateForm")
	public String update(com.mac.demo.model.User user, Model model) {
		User user2 = userSvc.getOne(user.getIdmac());
		model.addAttribute("user", user2);
		return "thymeleaf/mac/User/updateForm";
	}
	
//  유저 정보 수정
	@PutMapping("/updated")
	@ResponseBody
	public Map<String, Object> edit(User user, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		boolean result = userSvc.updated(user);
		map.put("result", result);
		return map;
	}
	
//	ID 중복체크
	@PostMapping("/idcheck")
	@ResponseBody
	public Map<String, Object> idcheck(@RequestParam("idMac")String idMac) {
		Map<String, Object> map = new HashMap<>();
		boolean result = userSvc.idcheck(idMac);
		map.put("result", result);
		map.put("id" , idMac);
		return map;
	}
	
//	email 인증 보내기
	@PostMapping("/checkmail")
	@ResponseBody
	public Map<String, Object> emailcheck(@RequestParam("emailMac")String emailMac) {
		Map<String, Object> map = new HashMap<>();
		String random = userSvc.checkmail(emailMac);
		if(random!=null) {
			map.put("result", true);
		} else {
			map.put("result", false);
		}
		map.put("code", random);
		map.put("emailMac", emailMac);
		return map;
	}
	
//	이메일 인증코드
	@PostMapping("/checkcode")
	@ResponseBody
	public Map<String, Object> checkcode(@RequestParam("code")String code) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", code);
		return map;
	}
	
//	닉네임
	@PostMapping("/nickCheck")
	@ResponseBody
	public Map<String, Object> nickCheck(@RequestParam("nick")String nick) {
		Map<String, Object> map = new HashMap<>();
		boolean result = userSvc.nickCheck(nick);
		map.put("result", result);
		return map;
	}
	
}
