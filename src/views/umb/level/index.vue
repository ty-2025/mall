<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-tickets"></i>
        <span>会员等级</span>
        <el-button size="mini" class="btn-add" @click="handleAdd()" style="margin-left: 20px">添加</el-button>
      </div>
    </el-card>
    <div class="table-container">
      <el-table ref="levelTable" :data="list" style="width: 100%;" v-loading="listLoading" border>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="等级名称" align="center">
          <template slot-scope="scope">{{ scope.row.name }}</template>
        </el-table-column>
        <el-table-column label="成长值" align="center">
          <template slot-scope="scope">{{ scope.row.growthPoint }}</template>
        </el-table-column>
        <el-table-column label="折扣" align="center">
          <template slot-scope="scope">{{ scope.row.discount }}折</template>
        </el-table-column>
        <el-table-column label="是否默认" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.$index, scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.defaultStatus"
              :disabled="scope.row.defaultStatus === 1">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleUpdate(scope.$index, scope.row)">编辑</el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.$index, scope.row)"
              :disabled="scope.row.defaultStatus === 1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
    </div>

    <!-- 添加/编辑会员等级对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      @close="handleDialogClose">

      <el-form
        :model="levelForm"
        :rules="levelRules"
        ref="levelForm"
        label-width="120px"
        size="small">

        <el-form-item label="等级名称" prop="name">
          <el-input v-model="levelForm.name" placeholder="请输入等级名称"></el-input>
        </el-form-item>

        <el-form-item label="成长值" prop="growthPoint">
          <el-input-number
            v-model="levelForm.growthPoint"
            :min="0"
            :max="100000"
            placeholder="请输入成长值"
            style="width: 100%"></el-input-number>
        </el-form-item>

        <el-form-item label="折扣" prop="discount">
          <el-input-number
            v-model="levelForm.discount"
            :min="1"
            :max="100"
            :precision="0"
            placeholder="请输入折扣"
            style="width: 100%">
            <template slot="append">折</template>
          </el-input-number>
        </el-form-item>

        <el-form-item label="免运费标准">
          <el-input-number
            v-model="levelForm.freeFreightPoint"
            :min="0"
            :precision="2"
            placeholder="请输入免运费标准"
            style="width: 100%">
            <template slot="append">元</template>
          </el-input-number>
        </el-form-item>

        <el-form-item label="评价成长值">
          <el-input-number
            v-model="levelForm.commentGrowthPoint"
            :min="0"
            :max="100"
            placeholder="每次评价获取的成长值"
            style="width: 100%"></el-input-number>
        </el-form-item>

        <el-form-item label="是否默认等级">
          <el-radio-group v-model="levelForm.defaultStatus">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="特权设置">
          <el-checkbox-group v-model="privileges">
            <el-checkbox label="freeFreight">免邮特权</el-checkbox>
            <el-checkbox label="signIn">签到特权</el-checkbox>
            <el-checkbox label="comment">评论获奖励</el-checkbox>
            <el-checkbox label="promotion">专享活动</el-checkbox>
            <el-checkbox label="memberPrice">会员价格</el-checkbox>
            <el-checkbox label="birthday">生日特权</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            type="textarea"
            v-model="levelForm.note"
            placeholder="请输入备注"
            :rows="3"></el-input>
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
import { fetchList, createMemberLevel, updateMemberLevel, deleteMemberLevel, updateMemberLevelStatus, getLevelDetail } from '@/api/memberLevel';

export default {
  name: 'memberLevel',
  data() {
    // 验证等级名称唯一性
    const validateName = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入等级名称'));
      } else {
        // 这里可以添加异步验证名称唯一性的逻辑
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

      // 对话框相关
      dialogVisible: false,
      dialogTitle: '添加会员等级',
      isEdit: false,
      submitLoading: false,
      privileges: [],
      levelForm: {
        id: null,
        name: '',
        growthPoint: 0,
        discount: 100,
        freeFreightPoint: 0,
        commentGrowthPoint: 0,
        defaultStatus: 0,
        priviledgeFreeFreight: 0,
        priviledgeSignIn: 0,
        priviledgeComment: 0,
        priviledgePromotion: 0,
        priviledgeMemberPrice: 0,
        priviledgeBirthday: 0,
        note: ''
      },
      levelRules: {
        name: [
          { required: true, message: '请输入等级名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
          { validator: validateName, trigger: 'blur' }
        ],
        growthPoint: [
          { required: true, message: '请输入成长值', trigger: 'blur' }
        ],
        discount: [
          { required: true, message: '请输入折扣', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    console.log('会员等级组件创建，开始获取数据...');
    this.getList();
  },
  watch: {
    privileges(newVal) {
      // 更新特权字段
      this.levelForm.priviledgeFreeFreight = newVal.includes('freeFreight') ? 1 : 0;
      this.levelForm.priviledgeSignIn = newVal.includes('signIn') ? 1 : 0;
      this.levelForm.priviledgeComment = newVal.includes('comment') ? 1 : 0;
      this.levelForm.priviledgePromotion = newVal.includes('promotion') ? 1 : 0;
      this.levelForm.priviledgeMemberPrice = newVal.includes('memberPrice') ? 1 : 0;
      this.levelForm.priviledgeBirthday = newVal.includes('birthday') ? 1 : 0;
    }
  },
  methods: {
    handleSizeChange(val) {
      this.listQuery.pageNum = 1;
      this.listQuery.pageSize = val;
      this.getList();
    },

    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
    },

    // 删除会员等级
    handleDelete(index, row) {
      if (row.defaultStatus === 1) {
        this.$message.warning('不能删除默认等级');
        return;
      }

      this.$confirm('是否要删除该会员等级?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMemberLevel(row.id).then(response => {
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
      this.dialogTitle = '添加会员等级';
      this.isEdit = false;
      this.dialogVisible = true;
    },

    // 打开编辑对话框
    handleUpdate(index, row) {
      this.dialogTitle = '编辑会员等级';
      this.isEdit = true;
      this.dialogVisible = true;
      this.loadLevelDetail(row.id);
    },

    // 加载等级详情
    loadLevelDetail(id) {
      getLevelDetail(id).then(response => {
        const level = response.data;
        this.levelForm = {
          id: level.id,
          name: level.name,
          growthPoint: level.growthPoint || 0,
          discount: level.discount || 100,
          freeFreightPoint: level.freeFreightPoint || 0,
          commentGrowthPoint: level.commentGrowthPoint || 0,
          defaultStatus: level.defaultStatus || 0,
          priviledgeFreeFreight: level.priviledgeFreeFreight || 0,
          priviledgeSignIn: level.priviledgeSignIn || 0,
          priviledgeComment: level.priviledgeComment || 0,
          priviledgePromotion: level.priviledgePromotion || 0,
          priviledgeMemberPrice: level.priviledgeMemberPrice || 0,
          priviledgeBirthday: level.priviledgeBirthday || 0,
          note: level.note || ''
        };

        // 设置特权复选框
        this.privileges = [];
        if (level.priviledgeFreeFreight === 1) this.privileges.push('freeFreight');
        if (level.priviledgeSignIn === 1) this.privileges.push('signIn');
        if (level.priviledgeComment === 1) this.privileges.push('comment');
        if (level.priviledgePromotion === 1) this.privileges.push('promotion');
        if (level.priviledgeMemberPrice === 1) this.privileges.push('memberPrice');
        if (level.priviledgeBirthday === 1) this.privileges.push('birthday');
      }).catch(error => {
        this.$message.error('加载会员等级详情失败: ' + (error.message || '未知错误'));
      });
    },

    // 提交表单
    handleSubmit() {
      this.$refs.levelForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true;

          const request = this.isEdit ?
            updateMemberLevel(this.levelForm) :
            createMemberLevel(this.levelForm);

          request.then(response => {
            this.submitLoading = false;
            this.$message.success(this.isEdit ? '编辑会员等级成功' : '添加会员等级成功');
            this.dialogVisible = false;
            this.getList(); // 刷新列表
          }).catch(error => {
            this.submitLoading = false;
            this.$message.error((this.isEdit ? '编辑会员等级失败: ' : '添加会员等级失败: ') + (error.message || '未知错误'));
          });
        }
      });
    },

    // 修改默认状态
    handleStatusChange(index, row) {
      if (row.defaultStatus === 1) {
        this.$message.warning('已经是默认等级');
        return;
      }

      updateMemberLevelStatus(row.id, row.defaultStatus).then(response => {
        this.$message({
          type: 'success',
          message: '状态更新成功!'
        });
        this.getList(); // 刷新列表，确保只有一个默认等级
      }).catch(error => {
        this.$message.error('状态更新失败: ' + (error.message || '未知错误'));
        // 回滚状态
        row.defaultStatus = row.defaultStatus === 1 ? 0 : 1;
      });
    },

    // 对话框关闭回调
    handleDialogClose() {
      this.$refs.levelForm.clearValidate();
      this.privileges = [];
      this.levelForm = {
        id: null,
        name: '',
        growthPoint: 0,
        discount: 100,
        freeFreightPoint: 0,
        commentGrowthPoint: 0,
        defaultStatus: 0,
        priviledgeFreeFreight: 0,
        priviledgeSignIn: 0,
        priviledgeComment: 0,
        priviledgePromotion: 0,
        priviledgeMemberPrice: 0,
        priviledgeBirthday: 0,
        note: ''
      };
    },

    getList() {
      this.listLoading = true;
      console.log('开始请求会员等级数据，参数:', this.listQuery);

      fetchList(this.listQuery).then(response => {
        this.listLoading = false;
        console.log('接口完整响应:', response);
        console.log('response.data:', response.data);

        // 多种数据格式适配
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

        console.log('从后端获取的列表数据:', this.list);
        console.log('总数:', this.total);

      }).catch((error) => {
        this.listLoading = false;
        console.error('获取会员等级列表失败:', error);
        this.$message.error('获取会员等级列表失败: ' + (error.message || '请检查网络连接'));
      });
    }
  }
};
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.table-container {
  margin-bottom: 20px;
}
.pagination-container {
  text-align: center;
  margin-top: 20px;
}
</style>
