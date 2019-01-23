package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/brand")
@RestController
public class BrandController {

    //从注册查询brandService对应的地址的代理对象
    @Reference
    private BrandService brandService;

    /**
     * 根据分页信息分页查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                  @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        return (List<TbBrand>) brandService.findPage(page,rows).getRows();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                               @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        return brandService.findPage(page,rows);
    }

    /**
     * 获取品牌列表
     * @return 品牌列表
     */
    @GetMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.queryAll();
    }
}
