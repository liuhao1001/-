package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.request.RequestSingleParam;
import myproject.common.sysLog.SysLog;
import myproject.common.utils.Result;
import myproject.convert.PostcategoriesConvert;
import myproject.entity.PostcategoriesEntity;
import myproject.query.PostcategoriesQuery;
import myproject.service.PostcategoriesService;
import myproject.vo.PostcategoriesVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 帖子话题分类
 */
@RestController
@RequestMapping("postcategories")
@Tag(name = "帖子话题分类")
@AllArgsConstructor
public class PostcategoriesController {
	private final PostcategoriesService postcategoriesService;

	@PostMapping("page")
	@Operation(summary = "分页")
	@SysLog(title = "查看帖子分类分页")
	public Result<PageResult<PostcategoriesVO>> page(@RequestBody @Valid PostcategoriesQuery query) {
		PageResult<PostcategoriesVO> page = postcategoriesService.page(query);
		return Result.ok(page);
	}

	@PostMapping("list")
	@Operation(summary = "列表")
	@SaIgnore
	@SysLog(title = "查看帖子分类列表")
	public Result<List<PostcategoriesVO>> list(@RequestBody @Valid PostcategoriesQuery query) {
		List<PostcategoriesVO> list = postcategoriesService.queryList(query);
		return Result.ok(list);
	}

	@PostMapping("/info")
	@Operation(summary = "信息")
	@SysLog(title = "查看帖子分类信息")
	public Result<PostcategoriesVO> get(@RequestSingleParam(value = "id") Long id) {
		PostcategoriesEntity entity = postcategoriesService.getById(id);
		return Result.ok(PostcategoriesConvert.INSTANCE.convert(entity));
	}

	@PostMapping("save")
	@Operation(summary = "保存")
	@SysLog(title = "保存帖子分类信息")
	public Result<String> save(@RequestBody PostcategoriesVO vo) {
		postcategoriesService.save(vo);
		return Result.ok();
	}

	@PostMapping("update")
	@Operation(summary = "修改")
	@SysLog(title = "修改帖子分类信息")
	public Result<String> update(@RequestBody @Valid PostcategoriesVO vo) {
		postcategoriesService.update(vo);
		return Result.ok();
	}

	@PostMapping("delete")
	@Operation(summary = "删除")
	@SysLog(title = "删除帖子分类信息")
	public Result<String> delete(@RequestBody List<Long> idList) {
		postcategoriesService.delete(idList);
		return Result.ok();
	}

	@PostMapping("export")
	@Operation(summary = "导出")
	@SysLog(title = "导出帖子分类信息")
	public void export(@RequestBody @Valid PostcategoriesQuery query) {
		postcategoriesService.export(query);
	}
}