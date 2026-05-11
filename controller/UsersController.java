package myproject.controller;


import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import myproject.common.captcha.CaptchaController;
import myproject.common.page.PageResult;
import myproject.common.request.RequestSingleParam;
import myproject.common.sysLog.SysLog;
import myproject.common.utils.FaceCatchUtils;
import myproject.common.utils.PwdUtil;
import myproject.common.utils.Result;
import myproject.common.utils.TokenEntity;
import myproject.constants.Constants;
import myproject.entity.UsersEntity;
import myproject.query.UsersQuery;
import myproject.service.UsersService;
import myproject.vo.SysAccountLoginVO;
import myproject.vo.Userpassvo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录相关
 */
@RequestMapping("user")
@RestController
@AllArgsConstructor
public class UsersController {

	private final UsersService userService;
	private final CaptchaController captchaController;
	private static final String StpPre = "user";

	/**
	 * 账号密码登录
	 */
	@SaIgnore
	@PostMapping(value = "/login")
	@SysLog(title = "用户登录", content = "系统日志")
	public Result<TokenEntity> login(@RequestBody SysAccountLoginVO sysAccountLoginVO) {
		//验证码
		captchaController.validateCaptcha(sysAccountLoginVO.getCaptcha(), sysAccountLoginVO.getKey());
		UsersEntity user = userService.getByUsername(sysAccountLoginVO.getUsername());
		if (user == null || !user.getPassword().equals(PwdUtil.encrypt(sysAccountLoginVO.getPassword()))) {
			return Result.error("账号或密码不正确");
		}
		StpUtil.login(StpPre + user.getId());
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUsername(user.getUsername());
		tokenEntity.setUserid(user.getId());
		tokenEntity.setRole(user.getRole());
		tokenEntity.setTablename("users");
		tokenEntity.setAddtime(user.getAddtime());
		tokenEntity.setAvatarurl(user.getAvatarurl());
		tokenEntity.setMoney(user.getMoney());
		tokenEntity.setName(user.getName());
		tokenEntity.setToken(StpUtil.getTokenValue());
		StpUtil.getSession().set(Constants.CURRENT_USER, tokenEntity);
		return Result.ok(tokenEntity);
	}

	/**
	 * @return myproject.common.utils.Result<java.lang.Boolean>
	 * @title: sendEmail
	 * @Description //通过邮箱登录--发送邮件
	 * @Param [sysAccountLoginVO]
	 **/
	@SaIgnore
	@PostMapping(value = "/sendEmail")
	public Result<Boolean> sendEmail(@RequestBody SysAccountLoginVO sysAccountLoginVO) {
		UsersEntity user = userService.getByUsername(sysAccountLoginVO.getUsername());
		if (StrUtil.isBlank(user.getEmail())) {
			return Result.error("用户邮箱为空,无法登录");
		}
		//发送邮箱验证码
		captchaController.sendEmail(user.getEmail());
		return Result.ok(true);
	}

	/**
	 * @return myproject.common.utils.Result<myproject.common.utils.TokenEntity>
	 * @title: loginByEmail
	 * @Description //邮箱登录
	 * @Param [sysAccountLoginVO]
	 **/
	@SaIgnore
	@PostMapping(value = "/loginByEmail")
	@SysLog(title = "用户邮箱登录", content = "系统日志")
	public Result<TokenEntity> loginByEmail(@RequestBody SysAccountLoginVO sysAccountLoginVO) {
		//验证码
		UsersEntity user = userService.getByUsername(sysAccountLoginVO.getUsername());
		captchaController.validateEmailCaptcha(sysAccountLoginVO.getCaptcha(), user.getEmail());
		StpUtil.login(StpPre + user.getId());
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUsername(user.getUsername());
		tokenEntity.setUserid(user.getId());
		tokenEntity.setRole(user.getRole());
		tokenEntity.setTablename("users");
		tokenEntity.setAddtime(user.getAddtime());
		tokenEntity.setAvatarurl(user.getAvatarurl());
		tokenEntity.setMoney(user.getMoney());
		tokenEntity.setToken(StpUtil.getTokenValue());
		tokenEntity.setName(user.getName());
		StpUtil.getSession().set(Constants.CURRENT_USER, tokenEntity);
		return Result.ok(tokenEntity);
	}

	/**
	 * @return myproject.common.utils.Result<myproject.common.utils.TokenEntity>
	 * @title: loginByEmail
	 * @Description //人脸图片登录
	 * @Param [sysAccountLoginVO]
	 **/
	@SaIgnore
	@PostMapping(value = "/loginByFaceCatch")
	public Result<TokenEntity> loginByFaceCatch(@RequestBody SysAccountLoginVO sysAccountLoginVO) throws IOException {
		//验证码
		UsersEntity user = userService.getByUsername(sysAccountLoginVO.getUsername());
		if (user == null) {
			return Result.error("用户不存在,请重新登录");
		}
		if (StrUtil.isBlank(user.getAvatarurl()) || StrUtil.isBlank(sysAccountLoginVO.getAvatarurl())) {
			return Result.error("用户头像为空,无法登录");
		}
		if (Boolean.FALSE.equals(FaceCatchUtils.isSimilar(user.getAvatarurl(), sysAccountLoginVO.getAvatarurl(), true))) {
			return Result.error("用户人脸不匹配,无法登录");
		}
		StpUtil.login(StpPre + user.getId());
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUsername(user.getUsername());
		tokenEntity.setUserid(user.getId());
		tokenEntity.setRole(user.getRole());
		tokenEntity.setTablename("users");
		tokenEntity.setAddtime(user.getAddtime());
		tokenEntity.setAvatarurl(user.getAvatarurl());
		tokenEntity.setMoney(user.getMoney());
		tokenEntity.setToken(StpUtil.getTokenValue());
		tokenEntity.setName(user.getName());
		StpUtil.getSession().set(Constants.CURRENT_USER, tokenEntity);
		return Result.ok(tokenEntity);
	}

	/**
	 * 注册
	 */
	@SaIgnore
	@PostMapping(value = "/register")
	@SysLog(title = "用户注册", content = "系统日志")
	public Result<Boolean> register(@RequestBody UsersEntity user) {
		if (userService.getOne(new QueryWrapper<UsersEntity>().eq("username", user.getUsername())) != null) {
			return Result.error("用户已存在");
		}
		user.setPassword(PwdUtil.encrypt(user.getPassword()));
		userService.save(user);
		return Result.ok(true);
	}


	/**
	 * 密码重置
	 */
	@SaIgnore
	@RequestMapping(value = "/resetPass")
	@SysLog(title = "密码重置", content = "系统日志")
	public Result resetPass(@RequestBody Userpassvo userpassvo) {
		UsersEntity user = userService.getOne(new QueryWrapper<UsersEntity>().eq("username", userpassvo.getUsername()));
		if (user == null) {
			return Result.error("账号不存在");
		}
		user.setPassword(PwdUtil.encrypt(Constants.RESETPASS));
		userService.updateById(user);
		return Result.ok("密码已重置为：123456");
	}

	/**
	 * 列表
	 */
	@PostMapping("/page")
	@SysLog(title = "查看用户列表分页")
	public Result page(@RequestBody UsersQuery user) {
		PageResult<UsersEntity> page = userService.queryPage(user);
		return Result.ok(page);
	}

	/**
	 * 列表
	 */
	@PostMapping("/list")
	@SysLog(title = "查看用户列表")
	public Result list(@RequestBody UsersEntity user) {
		QueryWrapper<UsersEntity> ew = new QueryWrapper<>(user);
		return Result.ok(userService.selectListView(ew));
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info")
	@SysLog(title = "查看用户信息")
	public Result<UsersEntity> info(@RequestSingleParam(value = "id") String id) {
		UsersEntity user = userService.getById(id);
		return Result.ok(user);
	}

	/**
	 * 获取用户的session用户信息
	 */
	@RequestMapping("/session")
	public Result getCurrUser(HttpServletRequest request) {
		return Result.ok(StpUtil.getSession().get(Constants.CURRENT_USER));
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	@SysLog(title = "保存用户")
	public Result save(@RequestBody UsersEntity user) {
		if (userService.getOne(new QueryWrapper<UsersEntity>().eq("username", user.getUsername())) != null) {
			return Result.error("用户已存在");
		}
		user.setPassword(PwdUtil.encrypt(user.getPassword()));
		userService.save(user);
		return Result.ok(true);
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@SysLog(title = "更新用户")
	public Result update(@RequestBody UsersEntity user) {
		if (StrUtil.isNotBlank(user.getPassword())) {
			user.setPassword(PwdUtil.encrypt(user.getPassword()));
		}
		userService.updateById(user);//全部更新
		return Result.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@SysLog(title = "删除用户")
	public Result delete(@RequestBody List<Long> idList) {
		userService.removeByIds(idList);
		return Result.ok();
	}

	/**
	 * （按值统计）
	 */
	@SaIgnore
	@RequestMapping("/value/{xColumnName}/{yColumnName}")
	public Result value(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		params.put("xColumn", xColumnName);
		params.put("yColumn", yColumnName);
		LambdaQueryWrapper<UsersEntity> wrapper = Wrappers.lambdaQuery();
		List<Map<String, Object>> result = userService.selectValue(params, wrapper);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Map<String, Object> m : result) {
			for (String k : m.keySet()) {
				if (m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date) m.get(k)));
				}
			}
		}
		return Result.ok(result);
	}

	/**
	 * （按值统计）时间统计类型
	 */
	@SaIgnore
	@RequestMapping("/value/{xColumnName}/{yColumnName}/{timeStatType}")
	public Result valueDay(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType, HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		params.put("xColumn", xColumnName);
		params.put("yColumn", yColumnName);
		params.put("timeStatType", timeStatType);
		LambdaQueryWrapper<UsersEntity> wrapper = Wrappers.lambdaQuery();
		List<Map<String, Object>> result = userService.selectTimeStatValue(params, wrapper);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Map<String, Object> m : result) {
			for (String k : m.keySet()) {
				if (m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date) m.get(k)));
				}
			}
		}
		return Result.ok(result);
	}

	/**
	 * 分组统计
	 */
	@SaIgnore
	@RequestMapping("/group/{columnName}")
	public Result group(@PathVariable("columnName") String columnName, HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		params.put("column", columnName);
		LambdaQueryWrapper<UsersEntity> wrapper = Wrappers.lambdaQuery();
		//		wrapper.orderByDesc(UsersEntity::getCount);
		List<Map<String, Object>> result = userService.selectGroup(params, wrapper);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Map<String, Object> m : result) {
			for (String k : m.keySet()) {
				if (m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date) m.get(k)));
				}
			}
		}
		return Result.ok(result);
	}
}
