<template>
  <div class="app-container">
    <el-card>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>会员</el-breadcrumb-item>
        <el-breadcrumb-item>积分记录</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="filter-container" style="margin-top: 20px;">
        <!-- 查询条件 -->
        <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="80px">
          <el-form-item label="会员ID" prop="memberId">
            <el-input
              v-model="queryParams.memberId"
              placeholder="请输入会员ID"
              clearable
              style="width: 200px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="queryParams.username"
              placeholder="请输入用户名"
              clearable
              style="width: 200px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="积分类型" prop="sourceType">
            <el-select v-model="queryParams.sourceType" placeholder="请选择积分类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="登录奖励" value="login" />
              <el-option label="签到奖励" value="signin" />
              <el-option label="消费获得" value="consume" />
              <el-option label="管理员修改" value="admin" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 积分记录表格 -->
        <el-table v-loading="loading" :data="integrationList" style="width: 100%; margin-top: 20px;">
          <el-table-column label="记录ID" align="center" prop="id" width="80" />
          <el-table-column label="会员ID" align="center" prop="memberId" width="80" />
          <el-table-column label="用户名" align="center" prop="username" />
          <el-table-column label="变更类型" align="center" prop="changeType">
            <template slot-scope="scope">
              <el-tag :type="scope.row.changeType === 'increase' ? 'success' : 'danger'">
                {{ scope.row.changeType === 'increase' ? '增加' : '减少' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="变更数量" align="center" prop="changeAmount">
            <template slot-scope="scope">
              <span :style="{ color: scope.row.changeType === 'increase' ? '#67C23A' : '#F56C6C' }">
                {{ scope.row.changeType === 'increase' ? '+' : '-' }}{{ scope.row.changeAmount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="来源类型" align="center" prop="sourceType">
            <template slot-scope="scope">
              {{ getSourceTypeText(scope.row.sourceType) }}
            </template>
          </el-table-column>
          <el-table-column label="操作人" align="center" prop="operator" />
          <el-table-column label="变更时间" align="center" prop="changeTime" width="180" />
          <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
        </el-table>

        <!-- 分页 -->
        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "IntegrationHistory",
  data() {
    return {
      loading: false,
      integrationList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        memberId: undefined,
        username: undefined,
        sourceType: undefined
      },
      // 完整的模拟数据
      mockData: [
        {
          id: 1,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 20,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-11-11 08:30:00',
          remark: '连续签到7天'
        },
        {
          id: 2,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-11-10 08:30:00',
          remark: '连续签到6天'
        },
        {
          id: 3,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-11-09 08:30:00',
          remark: '连续签到5天'
        },
        {
          id: 4,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-11-08 08:30:00',
          remark: '连续签到4天'
        },
        {
          id: 5,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-11-07 08:30:00',
          remark: '连续签到3天'
        },
        {
          id: 6,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-11-06 08:30:00',
          remark: '连续签到2天'
        },
        {
          id: 7,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-11-05 08:30:00',
          remark: '连续签到1天'
        },
        {
          id: 8,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-10-29 08:30:00',
          remark: '连续签到2天'
        },
        {
          id: 9,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'signin',
          operator: 'system',
          changeTime: '2025-10-28 08:30:00',
          remark: '连续签到1天'
        },
        {
          id: 10,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'login',
          operator: 'system',
          changeTime: '2025-11-11 09:00:00',
          remark: '每日登录奖励'
        },
        {
          id: 11,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 10,
          sourceType: 'login',
          operator: 'system',
          changeTime: '2025-11-10 09:00:00',
          remark: '每日登录奖励'
        },
        {
          id: 12,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 50,
          sourceType: 'consume',
          operator: 'system',
          changeTime: '2025-11-02 14:20:00',
          remark: '购物消费奖励'
        },
        {
          id: 13,
          memberId: 11,
          username: 'member',
          changeType: 'decrease',
          changeAmount: 30,
          sourceType: 'consume',
          operator: 'system',
          changeTime: '2025-11-01 16:45:00',
          remark: '积分兑换商品'
        },
        {
          id: 14,
          memberId: 11,
          username: 'member',
          changeType: 'increase',
          changeAmount: 100,
          sourceType: 'admin',
          operator: 'admin',
          changeTime: '2025-10-31 10:15:00',
          remark: '管理员手动添加'
        }
      ]
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;

      // 使用 setTimeout 模拟网络请求
      setTimeout(() => {
        let filteredData = [...this.mockData];

        // 应用筛选条件
        if (this.queryParams.memberId) {
          filteredData = filteredData.filter(item =>
            item.memberId && item.memberId.toString() === this.queryParams.memberId.toString()
          );
        }

        if (this.queryParams.username) {
          filteredData = filteredData.filter(item =>
            item.username && item.username.includes(this.queryParams.username)
          );
        }

        if (this.queryParams.sourceType) {
          filteredData = filteredData.filter(item =>
            item.sourceType === this.queryParams.sourceType
          );
        }

        // 分页处理
        const start = (this.queryParams.pageNum - 1) * this.queryParams.pageSize;
        const end = start + this.queryParams.pageSize;
        this.integrationList = filteredData.slice(start, end);
        this.total = filteredData.length;

        this.loading = false;
      }, 300);
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        memberId: undefined,
        username: undefined,
        sourceType: undefined
      };
      this.getList();
    },
    getSourceTypeText(type) {
      const typeMap = {
        'login': '登录奖励',
        'signin': '签到奖励',
        'consume': '消费获得',
        'admin': '管理员修改'
      };
      return typeMap[type] || type;
    }
  }
};
</script>

<style scoped>
.filter-container {
  padding: 20px;
}
.app-container {
  padding: 20px;
}
</style>
