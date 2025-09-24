package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.BrowsingHistory;
import com.atguigu.lease.web.app.mapper.BrowsingHistoryMapper;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liubo
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
        implements BrowsingHistoryService {

    @Autowired
    private BrowsingHistoryMapper browsingHistoryMapper;

    @Override
    public IPage<HistoryItemVo> pageHistoryItemByUserId(IPage<HistoryItemVo> page, Long userId) {
        return browsingHistoryMapper.pageHistoryItemByUserId(page,userId);
    }

    @Async
    @Override
    public void saveHistory(Long userId, Long roomId) {
        BrowsingHistory history = new BrowsingHistory();
        history.setUserId(userId);
        history.setRoomId(roomId);
        history.setBrowseTime(new Date());

        LambdaQueryWrapper<BrowsingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowsingHistory::getRoomId, roomId);
        queryWrapper.eq(BrowsingHistory::getUserId, userId);
        Long count = browsingHistoryMapper.selectCount(queryWrapper);

        //看有没有已经存在的记录，有就更新没有就插入
        if (count > 0) {
            LambdaUpdateWrapper<BrowsingHistory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BrowsingHistory::getRoomId, roomId);
            updateWrapper.eq(BrowsingHistory::getUserId, userId);
            browsingHistoryMapper.update(history,updateWrapper);
        }else{
            this.saveOrUpdate(history);
        }
    }
}




