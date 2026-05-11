package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.request.RequestSingleParam;
import myproject.common.sysLog.SysLog;
import myproject.common.utils.Result;
import myproject.convert.DiscussConvert;
import myproject.entity.DiscussEntity;
import myproject.query.DiscussQuery;
import myproject.service.DiscussService;
import myproject.vo.DiscussVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论表
 */
@RestController
@RequestMapping("discuss")
@Tag(name = "评论表")
@AllArgsConstructor
public class DiscussController {
	private final DiscussService discussService;

	@PostMapping("page")
	@Operation(summary = "分页")
	@SysLog(title = "查看评论分页")
	public Result<PageResult<DiscussVO>> page(@RequestBody DiscussQuery query) {
		PageResult<DiscussVO> page = discussService.page(query);
		return Result.ok(page);
	}

	@PostMapping("list")
	@Operation(summary = "查询评论列表")
	@SysLog(title = "查询评论列表")
	public Result<List<DiscussVO>> list(@RequestBody DiscussQuery query) {
		List<DiscussVO> list = discussService.queryList(query);
		return Result.ok(list);
	}

	@PostMapping("MyList")
	@Operation(summary = "查询自己评论列表")
	@SysLog(title = "查询自己评论列表")
	public Result<List<DiscussVO>> MyList(@RequestBody DiscussQuery query) {
		List<DiscussVO> list = discussService.MyList(query);
		return Result.ok(list);
	}

	@PostMapping("tree")
	@Operation(summary = "查询评论树")
	@SysLog(title = "查询评论树")
	public Result<List<DiscussVO>> tree(@RequestBody DiscussQuery query) {
		List<DiscussVO> list = discussService.queryTree(query);
		return Result.ok(list);
	}

	@PostMapping("like")
	@Operation(summary = "点赞")
	@SysLog(title = "点赞评论")
	public Result<Boolean> like(@RequestSingleParam("id") Long id) {
		discussService.like(id);
		return Result.ok(true);
	}

	@PostMapping("/info")
	@Operation(summary = "信息")
	@SysLog(title = "查看评论信息")
	public Result<DiscussVO> get(@RequestSingleParam("id") Long id) {
		DiscussEntity entity = discussService.getById(id);
		return Result.ok(DiscussConvert.INSTANCE.convert(entity));
	}

	@PostMapping("save")
	@Operation(summary = "保存")
	@SysLog(title = "评论")
	public Result<String> save(@RequestBody DiscussVO vo) {
		discussService.save(vo);
		return Result.ok();
	}

	@PostMapping("update")
	@Operation(summary = "修改")
	@SysLog(title = "修改评论")
	public Result<String> update(@RequestBody DiscussVO vo) {
		discussService.update(vo);
		return Result.ok();
	}

	@PostMapping("delete")
	@Operation(summary = "删除")
	@SysLog(title = "删除评论")
	public Result<String> delete(@RequestBody List<Long> idList) {
		discussService.delete(idList);
		return Result.ok();
	}

	@PostMapping("export")
	@Operation(summary = "导出")
	@SysLog(title = "导出评论表列表")
	public void export(@RequestBody DiscussQuery query) {
		discussService.export(query);
	}

	@PostMapping("import")
	@Operation(summary = "导入")
	public Result<String> importdiscuss(@RequestSingleParam(value = "file") String file) {
		if (file.isEmpty()) {
			return Result.error("请选择需要上传的文件");
		}
		file = file.replace("api/", "");
		File importFile = new File(file);
		discussService.importdiscuss(importFile);
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
		LambdaQueryWrapper<DiscussEntity> wrapper = Wrappers.lambdaQuery();
		List<Map<String, Object>> result = discussService.selectValue(params, wrapper);
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
		LambdaQueryWrapper<DiscussEntity> wrapper = Wrappers.lambdaQuery();
		List<Map<String, Object>> result = discussService.selectTimeStatValue(params, wrapper);
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
		LambdaQueryWrapper<DiscussEntity> wrapper = Wrappers.lambdaQuery();
		//		wrapper.orderByDesc(DiscussEntity::getCount);
		List<Map<String, Object>> result = discussService.selectGroup(params, wrapper);
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