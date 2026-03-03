/**
 * API 使用示例
 * 展示如何在实际项目中使用封装的 API
 */
import { authApi, transactionApi, chartApi, budgetApi, categoryApi } from './index.js'

/**
 * 示例 1: 用户登录
 */
export const handleLogin = async (phone, smsCode) => {
  try {
    // 调用短信验证码登录接口
    const res = await authApi.smsLogin({
      phone,
      smsCode
    })
    
    // 登录成功，保存 token
    if (res.data && res.data.token) {
      uni.setStorageSync('token', res.data.token)
      uni.setStorageSync('userInfo', res.data.user)
    }
    
    // 跳转到首页
    uni.switchTab({
      url: '/pages/tabBar/bill/index.vue'
    })
    
    return res
  } catch (error) {
    console.error('登录失败:', error)
    throw error
  }
}

/**
 * 示例 2: 创建交易记录
 */
export const handleCreateTransaction = async (transactionData) => {
  try {
    const res = await transactionApi.createTransaction(transactionData)
    
    // 提示成功
    uni.showToast({
      title: '记账成功',
      icon: 'success'
    })
    
    // 返回账单首页
    uni.switchTab({
      url: '/pages/tabBar/bill/index.vue'
    })
    
    return res
  } catch (error) {
    console.error('创建交易失败:', error)
    throw error
  }
}

/**
 * 示例 3: 获取月度账单列表
 */
export const loadMonthlyTransactions = async (year, month) => {
  try {
    const res = await transactionApi.getMonthlyTransactions({
      year,
      month,
      page: 1,
      pageSize: 50
    })
    
    return res.data
  } catch (error) {
    console.error('获取账单失败:', error)
    return null
  }
}

/**
 * 示例 4: 获取图表数据
 */
export const loadChartData = async (period, type, year, month) => {
  try {
    // 并行请求多个图表数据
    const [trendRes, pieRes, rankingRes] = await Promise.all([
      chartApi.getTrendData({ period, type, year, month }),
      chartApi.getPieData({ period, type, year, month }),
      chartApi.getRankingData({ period, type, year, month, limit: 10 })
    ])
    
    return {
      trendData: trendRes.data,
      pieData: pieRes.data,
      rankingData: rankingRes.data
    }
  } catch (error) {
    console.error('获取图表数据失败:', error)
    return null
  }
}

/**
 * 示例 5: 设置预算
 */
export const handleSetBudget = async (categoryId, month, amount, alertThreshold = 0.8) => {
  try {
    const res = await budgetApi.setCategoryBudget({
      categoryId,
      month,
      amount,
      alertThreshold
    })
    
    uni.showToast({
      title: '预算设置成功',
      icon: 'success'
    })
    
    return res
  } catch (error) {
    console.error('设置预算失败:', error)
    throw error
  }
}

/**
 * 示例 6: 获取预算使用情况
 */
export const loadBudgetStatus = async (month) => {
  try {
    const res = await budgetApi.getBudgetStatus({ month })
    return res.data
  } catch (error) {
    console.error('获取预算状态失败:', error)
    return null
  }
}

/**
 * 示例 7: 获取分类列表
 */
export const loadCategories = async (type = 'expense') => {
  try {
    const res = await categoryApi.getCategories({
      type,
      includeSystem: true
    })
    
    return res.data
  } catch (error) {
    console.error('获取分类失败:', error)
    return null
  }
}

/**
 * 示例 8: 添加自定义分类
 */
export const handleAddCategory = async (categoryData) => {
  try {
    const res = await categoryApi.addCategory(categoryData)
    
    uni.showToast({
      title: '分类添加成功',
      icon: 'success'
    })
    
    return res
  } catch (error) {
    console.error('添加分类失败:', error)
    throw error
  }
}

/**
 * 示例 9: 删除交易记录
 */
export const handleDeleteTransaction = async (transactionId) => {
  try {
    // 二次确认
    await new Promise((resolve, reject) => {
      uni.showModal({
        title: '提示',
        content: '确定要删除这条记录吗？',
        success: (res) => {
          if (res.confirm) {
            resolve()
          } else {
            reject(new Error('取消删除'))
          }
        }
      })
    })
    
    // 调用删除接口
    await transactionApi.deleteTransaction(transactionId)
    
    uni.showToast({
      title: '删除成功',
      icon: 'success'
    })
    
    return true
  } catch (error) {
    if (error.message !== '取消删除') {
      console.error('删除失败:', error)
    }
    throw error
  }
}

/**
 * 示例 10: 获取完整的首页数据
 */
export const loadBillPageData = async (year, month) => {
  try {
    // 并行请求多个数据源
    const [transactionsRes, statsRes, budgetRes] = await Promise.all([
      transactionApi.getMonthlyTransactions({ year, month }),
      transactionApi.getTransactionStats({ year, month }),
      budgetApi.getBudgetStatus({ month: `${year}-${String(month).padStart(2, '0')}` })
    ])
    
    return {
      transactions: transactionsRes.data,
      stats: statsRes.data,
      budget: budgetRes.data
    }
  } catch (error) {
    console.error('获取首页数据失败:', error)
    return null
  }
}

// 导出所有示例方法
export default {
  handleLogin,
  handleCreateTransaction,
  loadMonthlyTransactions,
  loadChartData,
  handleSetBudget,
  loadBudgetStatus,
  loadCategories,
  handleAddCategory,
  handleDeleteTransaction,
  loadBillPageData
}
