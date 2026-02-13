export const copyToClipboard = async (text: string) => {
  // 1. 首先尝试使用现代 Clipboard API
  if (navigator.clipboard && window.isSecureContext) {
    try {
      await navigator.clipboard.writeText(text);
      return { success: true, method: 'clipboard-api' };
    } catch (error) {
      console.warn('Clipboard API 失败，尝试降级方法:', error);
      // 如果失败，继续尝试降级方案
    }
  }

  // 2. 降级方案：使用 textarea + execCommand
  try {
    // 创建一个临时的 textarea 元素
    const textArea = document.createElement('textarea');
    textArea.value = text;

    // 防止在iOS上缩放
    textArea.style.fontSize = '12pt';
    // 重置盒模型
    textArea.style.border = '0';
    textArea.style.padding = '0';
    textArea.style.margin = '0';
    // 使元素绝对定位，脱离可视区域
    textArea.style.position = 'absolute';
    textArea.style.left = '-9999px';

    // 获取当前滚动位置，防止页面滚动
    const scrollY = window.scrollY || document.documentElement.scrollTop;
    const isRTL = document.documentElement.getAttribute('dir') === 'rtl';
    if (isRTL) {
      textArea.style.right = '-9999px';
    }

    // 将 textarea 添加到页面中
    document.body.append(textArea);

    // 选中文本
    textArea.select();
    textArea.setSelectionRange(0, textArea.value.length); // 兼容移动设备

    // 执行复制命令
    const success = document.execCommand('copy');

    // 移除 textarea
    textArea.remove();

    // 恢复滚动位置
    window.scrollTo(0, scrollY);

    if (success) {
      return { success: true, method: 'execCommand' };
    } else {
      throw new Error('execCommand 复制失败');
    }
  } catch (error: any) {
    console.error('所有复制方法均失败:', error);
    return {
      success: false,
      method: null,
      error: error.message,
    };
  }
};
