<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.username" placeholder="用户名" style="width: 200px;" class="filter-item" />
      <el-input v-model="listQuery.phone" placeholder="手机号" style="width: 200px;" class="filter-item" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" type="default" icon="el-icon-refresh" @click="resetFilter">
        重置
      </el-button>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column label="ID" prop="id" align="center" width="80">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户名" min-width="150">
        <template slot-scope="{row}">
          <span>{{ row.username }}</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" min-width="150">
        <template slot-scope="{row}">
          <span>{{ row.phone }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当前积分" width="120" align="center">
        <template slot-scope="{row}">
          <span>{{ row.integration || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会员等级" width="120" align="center">
        <template slot-scope="{row}">
          <span>{{ row.levelName || '普通会员' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="160" align="center">
        <template slot-scope="{row}">
          <span>{{ row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleViewDetail(row)">
            查看详情
          </el-button>
          <el-button type="success" size="mini" @click="handleIntegrationDetail(row)">
            积分明细
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-show="total>0" class="pagination-container">
      <el-pagination
        background
        :current-page="listQuery.page"
        :page-size="listQuery.limit"
        :total="total"
        layout="prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog title="会员详情" :visible.sync="detailDialogVisible" width="600px">
      <el-form ref="detailForm" :model="memberDetail" label-width="100px" disabled>
        <el-form-item label="用户名">
          <el-input v-model="memberDetail.username" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="memberDetail.phone" />
        </el-form-item>
        <el-form-item label="当前积分">
          <el-input v-model="memberDetail.integration" />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-input v-model="memberDetail.levelName" />
        </el-form-item>
        <el-form-item label="注册时间">
          <el-input v-model="memberDetail.createTime" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="积分明细" :visible.sync="integrationDialogVisible" width="800px">
      <el-table :data="integrationList" v-loading="integrationLoading">
        <el-table-column prop="changeType" label="变更类型" width="120">
          <template slot-scope="{row}">
            <el-tag :type="row.changeType === '增加' ? 'success' : 'danger'">
              {{ row.changeType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="changeCount" label="变更积分" width="120" align="center">
          <template slot-scope="{row}">
            <span :style="{color: row.changeType === '增加' ? '#67C23A' : '#F56C6C'}">
              {{ row.changeType === '增加' ? '+' : '-' }}{{ row.changeCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="sourceType" label="来源类型" width="150">
          <template slot-scope="{row}">
            {{ getSourceTypeText(row.sourceType) }}
          </template>
        </el-table-column>
        <el-table-column prop="note" label="备注"></el-table-column>
        <el-table-column prop="createTime" label="变更时间" width="160">
          <template slot-scope="{row}">
            {{ row.createTime }}
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="integrationDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchMemberList, fetchMemberDetail, fetchMemberIntegration } from '@/api/member'

export default {
  name: 'MemberList',
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        username: undefined,
        phone: undefined
      },
      memberDetail: {},
      detailDialogVisible: false,
      integrationDialogVisible: false,
      integrationList: [],
      integrationLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const response = await fetchMemberList(this.listQuery)
        this.list = response.data.list || response.data
        this.total = response.data.total || 0
      } catch (error) {
        console.error('获取会员列表失败:', error)
      } finally {
        this.listLoading = false
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetFilter() {
      this.listQuery = {
        page: 1,
        limit: 20,
        username: undefined,
        phone: undefined
      }
      this.getList()
    },
    handlePageChange(page) {
      this.listQuery.page = page
      this.getList()
    },
    async handleViewDetail(row) {
      try {
        const response = await fetchMemberDetail(row.id)
        this.memberDetail = response.data
        this.detailDialogVisible = true
      } catch (error) {
        console.error('获取会员详情失败:', error)
      }
    },
    async handleIntegrationDetail(row) {
      this.integrationLoading = true
      this.integrationDialogVisible = true
      try {
        const response = await fetchMemberIntegration(row.id)
        this.integrationList = response.data.list || response.data
      } catch (error) {
        console.error('获取积分明细失败:', error)
        this.integrationList = []
      } finally {
        this.integrationLoading = false
      }
    },
    getSourceTypeText(sourceType) {
      const typeMap = {
        'register': '用户注册',
        'login': '每日登录',
        'order': '订单消费',
        'refund': '订单退款',
        'admin': '管理员调整'
      }
      return typeMap[sourceType] || sourceType
    }
  }
}
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
.pagination-container {
  margin-top: 20px;
  text-align: center;
}
</style>
