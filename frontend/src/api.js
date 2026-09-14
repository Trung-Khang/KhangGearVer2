import axios from 'axios'

const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE || '' })
let csrfToken
api.interceptors.request.use(async (config) => {
  if (!['get', 'head', 'options'].includes((config.method || 'get').toLowerCase()) && !csrfToken) {
    const response = await api.get('/api/csrf')
    csrfToken = response.data.token
  }
  if (csrfToken) config.headers['X-CSRF-TOKEN'] = csrfToken
  return config
})
export const mediaUrl = (path) => path ? `${api.defaults.baseURL}${path}` : null
export const getCategories = () => api.get('/api/storefront/categories').then((response) => response.data)
export const getProducts = (params = {}) => api.get('/api/storefront/products', { params }).then((response) => response.data)
export const getProduct = (id) => api.get(`/api/storefront/products/${id}`).then((response) => response.data)
export const getFeatured = () => api.get('/api/storefront/featured').then((response) => response.data)
export const getCart = () => api.get('/api/cart').then((response) => response.data)
export const addToCart = (productId, quantity = 1) => api.post('/api/cart/items', { productId, quantity }).then((response) => response.data)
export const updateCart = (productId, quantity) => api.put(`/api/cart/items/${productId}`, { productId, quantity }).then((response) => response.data)
export const removeFromCart = (productId) => api.delete(`/api/cart/items/${productId}`).then((response) => response.data)
export const checkout = (payload) => api.post('/api/cart/checkout', payload).then((response) => response.data)
export const getProfile = () => api.get('/api/account/profile').then((response) => response.data)
export const getOrders = () => api.get('/api/account/orders').then((response) => response.data)
export const cancelOrder = (id) => api.post(`/api/account/orders/${id}/cancel`).then((response) => response.data)
export default api
