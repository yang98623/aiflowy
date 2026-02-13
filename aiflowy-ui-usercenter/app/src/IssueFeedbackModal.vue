<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { reactive, ref } from 'vue';
import { useRoute } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElSelect,
} from 'element-plus';
import { tryit } from 'radash';

import Upload from '#/components/upload/Upload.vue';

import { api } from './api/request';

const options = [
  {
    value: 1,
    label: '功能故障',
  },
  {
    value: 2,
    label: '优化建议',
  },
  {
    value: 3,
    label: '账号问题',
  },
  {
    value: 4,
    label: '其它',
  },
];

const route = useRoute();
const showDialog = ref(false);
const formRef = ref<FormInstance>();
const formData = reactive({
  feedbackType: '',
  feedbackContent: '',
  contactInfo: '',
  attachmentUrl: '',
});
const loading = ref(false);
const uploadRef = ref();

function uploadSuccess(res: any) {
  formData.attachmentUrl = res;
}
function onRemove() {
  formData.attachmentUrl = '';
}
const handleSubmit = async () => {
  const isValid = await formRef.value?.validate();

  if (isValid) {
    loading.value = true;

    const [, res] = await tryit(api.post)(
      '/userCenter/sysUserFeedback/save',
      formData,
    );

    if (res && res.errorCode === 0) {
      ElMessage.success('反馈成功！');
      showDialog.value = false;
      formRef.value?.resetFields();
      uploadRef.value?.clear();
    }
    loading.value = false;
  }
};
</script>

<template>
  <Teleport
    v-if="!route.path.includes('auth') && !route.path.includes('share')"
    to="#app"
  >
    <div
      class="fixed bottom-1 right-2 cursor-pointer text-6xl active:opacity-70"
      @click="showDialog = !showDialog"
    >
      <IconifyIcon icon="svg:issue" />
    </div>
  </Teleport>
  <ElDialog
    draggable
    v-model="showDialog"
    title="问题反馈"
    style="max-width: 560px"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      label-width="80px"
      label-position="left"
      require-asterisk-position="right"
    >
      <ElFormItem
        prop="feedbackType"
        label="问题类型"
        :rules="[{ required: true, message: '此为必填项' }]"
      >
        <ElSelect
          :options="options"
          v-model="formData.feedbackType"
          placeholder="请选择问题类型"
        />
      </ElFormItem>
      <ElFormItem
        prop="feedbackContent"
        label="问题描述"
        :rules="[{ required: true, message: '此为必填项' }]"
      >
        <ElInput
          type="textarea"
          :rows="5"
          v-model="formData.feedbackContent"
          placeholder="请简要描述下您所遇到的问题"
        />
      </ElFormItem>
      <ElFormItem prop="contactInfo" label="联系方式">
        <ElInput
          v-model="formData.contactInfo"
          placeholder="请留下手机号/邮箱"
        />
      </ElFormItem>
      <ElFormItem prop="attachmentUrl" label="上传附件">
        <Upload
          ref="uploadRef"
          @success="uploadSuccess"
          @handle-delete="onRemove"
        />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="showDialog = false">取消</ElButton>
        <ElButton type="primary" :loading="loading" @click="handleSubmit">
          立即反馈
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>
