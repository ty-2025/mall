<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button style="float:right" type="primary" @click="handleSearchList()" size="small">查询搜索</el-button>
        <el-button style="float:right;margin-right: 15px" @click="handleResetSearch()" size="small">重置</el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input v-model="listQuery.keyword" class="input-width" placeholder="用户名/手机号" clearable></el-input>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>会员列表</span>
      <el-button size="mini" class="btn-add" @click="handleAdd()" style="margin-left: 20px">添加</el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="memberTable" :data="list" v-loading="listLoading" border>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="用户名" align="center">
          <template slot-scope="scope">{{ scope.row.username }}</template>
        </el-table-column>
        <el-table-column label="手机号" align="center">
          <template slot-scope="scope">{{ scope.row.phone }}</template>
        </el-table-column>
        <el-table-column label="会员等级" align="center">
          <template slot-scope="scope">{{ getMemberLevelName(scope.row.memberLevelId) }}</template>
        </el-table-column>
        <el-table-column label="注册时间" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.createTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="最后登录" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.loginTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.$index, scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.status">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleUpdate(scope.$index, scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10,15,20]"
        :total="total">
      </el-pagination>
    </div>

    <!-- 添加/编辑会员对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      @close="handleDialogClose">

      <el-form
        :model="memberForm"
        :rules="memberRules"
        ref="memberForm"
        label-width="100px"
        size="small">

        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="memberForm.username"
            placeholder="请输入用户名"
            :disabled="isEdit"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input
            v-model="memberForm.password"
            type="password"
            placeholder="请输入密码"
            show-password></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword" v-if="!isEdit">
          <el-input
            v-model="memberForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password></el-input>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="memberForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="memberForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>

        <el-form-item label="会员等级" prop="memberLevelId">
          <el-select v-model="memberForm.memberLevelId" placeholder="请选择会员等级" style="width: 100%">
            <el-option
              v-for="level in memberLevelOptions"
              :key="level.id"
              :label="level.name"
              :value="level.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="memberForm.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="生日">
          <el-date-picker
            v-model="memberForm.birthday"
            type="date"
            placeholder="选择生日"
            value-format="yyyy-MM-dd"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="所在城市">
          <el-input v-model="memberForm.city" placeholder="请输入所在城市"></el-input>
        </el-form-item>

        <el-form-item label="职业">
          <el-input v-model="memberForm.job" placeholder="请输入职业"></el-input>
        </el-form-item>

        <el-form-item label="个性签名">
          <el-input
            type="textarea"
            v-model="memberForm.personalizedSignature"
            placeholder="请输入个性签名"
            :rows="3"></el-input>
        </el-form-item>

        <el-form-item label="状态" v-if="isEdit">
          <el-radio-group v-model="memberForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          :loading="submitLoading"
          size="small">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, createMember, updateMember, deleteMember, updateMemberStatus, getMemberDetail } from '@/api/member';
import { fetchList as fetchLevelList } from '@/api/memberLevel';
import { formatDate } from '@/utils/date';

export default {
  name: 'memberList',
  data() {
    // 验证确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.memberForm.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };

    // 验证手机号
    const validatePhone = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入手机号'));
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };

    return {
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        keyword: null
      },
      list: [],
      total: 0,
      listLoading: false,
      memberLevels: {
        1: '普通会员',
        2: '黄金会员',
        3: '白金会员',
        4: '钻石会员'
      },

      // 对话框相关
      dialogVisible: false,
      dialogTitle: '添加会员',
      isEdit: false,
      submitLoading: false,
      memberLevelOptions: [],
      memberForm: {
        id: null,
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        phone: '',
        memberLevelId: 1,
        gender: 0,
        birthday: '',
        city: '',
        job: '',
        personalizedSignature: '',
        status: 1
      },
      memberRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        memberLevelId: [
          { required: true, message: '请选择会员等级', trigger: 'change' }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') {
        return 'N/A';
      }
      let date = new Date(time);
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss');
    }
  },
  methods: {
    getMemberLevelName(levelId) {
      return this.memberLevels[levelId] || '普通会员';
    },

    handleResetSearch() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        keyword: null
      };
      this.getList();
    },

    handleSearchList() {
      this.listQuery.pageNum = 1;
      this.getList();
    },

    handleSizeChange(val) {
      this.listQuery.pageNum = 1;
      this.listQuery.pageSize = val;
      this.getList();
    },

    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
    },

    // 删除会员
    handleDelete(index, row) {
      this.$confirm('是否要删除该会员?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMember(row.id).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getList();
        }).catch(error => {
          this.$message.error('删除失败: ' + (error.message || '未知错误'));
        });
      });
    },

    // 打开添加对话框
    handleAdd() {
      this.dialogTitle = '添加会员';
      this.isEdit = false;
      this.dialogVisible = true;
      this.loadMemberLevels();
    },

    // 打开编辑对话框
    handleUpdate(index, row) {
      this.dialogTitle = '编辑会员';
      this.isEdit = true;
      this.dialogVisible = true;
      this.loadMemberDetail(row.id);
      this.loadMemberLevels();
    },

    // 加载会员详情
    loadMemberDetail(id) {
      getMemberDetail(id).then(response => {
        const member = response.data;
        this.memberForm = {
          id: member.id,
          username: member.username,
          password: '',
          confirmPassword: '',
          nickname: member.nickname || '',
          phone: member.phone || '',
          memberLevelId: member.memberLevelId || 1,
          gender: member.gender || 0,
          birthday: member.birthday || '',
          city: member.city || '',
          job: member.job || '',
          personalizedSignature: member.personalizedSignature || '',
          status: member.status || 1
        };
      }).catch(error => {
        this.$message.error('加载会员详情失败: ' + (error.message || '未知错误'));
      });
    },

    // 加载会员等级选项
    loadMemberLevels() {
      fetchLevelList({ pageSize: 100 }).then(response => {
        if (response.data && Array.isArray(response.data)) {
          this.memberLevelOptions = response.data;
        } else if (response.data && response.data.list) {
          this.memberLevelOptions = response.data.list;
        }
      }).catch(error => {
        console.error('加载会员等级失败:', error);
        this.$message.error('加载会员等级失败');
      });
    },

    // 提交表单
    handleSubmit() {
      this.$refs.memberForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true;

          // 准备提交数据
          const submitData = { ...this.memberForm };
          delete submitData.confirmPassword;

          // 如果是编辑且密码为空，则不更新密码
          if (this.isEdit && (!submitData.password || submitData.password === '')) {
            delete submitData.password;
          }

          const request = this.isEdit ?
            updateMember(submitData) :
            createMember(submitData);

          request.then(response => {
            this.submitLoading = false;
            this.$message.success(this.isEdit ? '编辑会员成功' : '添加会员成功');
            this.dialogVisible = false;
            this.getList(); // 刷新列表
          }).catch(error => {
            this.submitLoading = false;
            this.$message.error((this.isEdit ? '编辑会员失败: ' : '添加会员失败: ') + (error.message || '未知错误'));
          });
        }
      });
    },

    // 修改状态
    handleStatusChange(index, row) {
      updateMemberStatus(row.id, row.status).then(response => {
        this.$message({
          type: 'success',
          message: '状态更新成功!'
        });
      }).catch(error => {
        this.$message.error('状态更新失败: ' + (error.message || '未知错误'));
        // 回滚状态
        row.status = row.status === 1 ? 0 : 1;
      });
    },

    // 对话框关闭回调
    handleDialogClose() {
      this.$refs.memberForm.clearValidate();
      this.memberForm = {
        id: null,
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        phone: '',
        memberLevelId: 1,
        gender: 0,
        birthday: '',
        city: '',
        job: '',
        personalizedSignature: '',
        status: 1
      };
    },

    getList() {
      this.listLoading = true;
      fetchList(this.listQuery).then(response => {
        this.listLoading = false;

        let data = response.data;
        if (data && Array.isArray(data)) {
          this.list = data;
          this.total = data.length;
        } else if (data && data.list) {
          this.list = data.list;
          this.total = data.total || data.list.length;
        } else {
          this.list = [];
          this.total = 0;
        }

      }).catch((error) => {
        this.listLoading = false;
        this.$message.error('获取会员列表失败: ' + (error.message || '请检查网络连接'));
      });
    }
  }
};
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.operate-container {
  margin-bottom: 20px;
}
.table-container {
  margin-bottom: 20px;
}
.pagination-container {
  text-align: center;
}
.input-width {
  width: 200px;
}
</style>
