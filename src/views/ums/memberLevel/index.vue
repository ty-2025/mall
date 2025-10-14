<template>
  <div class="app-container">
    <div class="filter-container">
      <el-button class="filter-item" type="primary" icon="el-icon-plus" @click="handleCreate">
        添加等级
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
      <el-table-column label="等级名称" min-width="150">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成长值要求" width="120" align="center">
        <template slot-scope="{row}">
          <span>{{ row.growthPoint }}</span>
        </template>
      </el-table-column>
      <el-table-column label="默认等级" width="100" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.defaultStatus ? 'success' : 'info'">
            {{ row.defaultStatus ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="特权免运费" width="120" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.freeFreightFlag ? 'success' : 'info'">
            {{ row.freeFreightFlag ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评论获奖励" width="120" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.commentGrowthPoint ? 'success' : 'info'">
            {{ row.commentGrowthPoint ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button v-if="!row.defaultStatus" type="danger" size="mini" @click="handleDelete(row)">
            删除
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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="500px">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="120px">
        <el-form-item label="等级名称" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="成长值要求" prop="growthPoint">
          <el-input-number v-model="temp.growthPoint" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="默认等级">
          <el-switch v-model="temp.defaultStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="特权免运费">
          <el-switch v-model="temp.freeFreightFlag" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="评论获奖励">
          <el-switch v-model="temp.commentGrowthPoint" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="特权详情">
          <el-input v-model="temp.priviledge" type="textarea" :rows="3" placeholder="请输入等级特权详情" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">
          确认
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchMemberLevelList, createMemberLevel, updateMemberLevel, deleteMemberLevel } from '@/api/member'

export default {
  name: 'MemberLevel',
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20
      },
      temp: {
        id: undefined,
        name: '',
        growthPoint: 0,
        defaultStatus: 0,
        freeFreightFlag: 0,
        commentGrowthPoint: 0,
        priviledge: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑会员等级',
        create: '添加会员等级'
      },
      rules: {
        name: [{ required: true, message: '等级名称不能为空', trigger: 'blur' }],
        growthPoint: [{ required: true, message: '成长值要求不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const response = await fetchMemberLevelList(this.listQuery)
        this.list = response.data.list || response.data
        this.total = response.data.total || 0
      } catch (error) {
        console.error('获取会员等级列表失败:', error)
      } finally {
        this.listLoading = false
      }
    },
    handlePageChange(page) {
      this.listQuery.page = page
      this.getList()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        name: '',
        growthPoint: 0,
        defaultStatus: 0,
        freeFreightFlag: 0,
        commentGrowthPoint: 0,
        priviledge: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate(async(valid) => {
        if (valid) {
          try {
            await createMemberLevel(this.temp)
            this.dialogFormVisible = false
            this.$message.success('添加成功')
            this.getList()
          } catch (error) {
            console.error('添加会员等级失败:', error)
          }
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(async(valid) => {
        if (valid) {
          try {
            await updateMemberLevel(this.temp)
            this.dialogFormVisible = false
            this.$message.success('更新成功')
            this.getList()
          } catch (error) {
            console.error('更新会员等级失败:', error)
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除该会员等级吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        try {
          await deleteMemberLevel(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error('删除会员等级失败:', error)
        }
      })
    }
  }
}
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: center;
}
</style>
