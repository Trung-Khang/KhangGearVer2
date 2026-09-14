import axios from 'axios'

const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE || '' })
export const mediaUrl = (path) => path ? `${api.defaults.baseURL}${path}` : null
export const getCategories = () => api.get('/api/storefront/categories').then((response) => response.data)
export const getProducts = (params = {}) => api.get('/api/storefront/products', { params }).then((response) => response.data)
export const getProduct = (id) => api.get(`/api/storefront/products/${id}`).then((response) => response.data)
export const getFeatured = () => api.get('/api/storefront/featured').then((response) => response.data)
export default api
