import axios, { type AxiosInstance } from 'axios';
import { config } from '../config';

// Create axios instance with auth interceptors
const createAuthenticatedAxios = (baseURL: string): AxiosInstance => {
    const instance = axios.create({
        baseURL,
        headers: {
            'Content-Type': 'application/json',
        },
    });

    instance.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem('cinema_token');
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    instance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response && error.response.status === 401) {
                localStorage.removeItem('cinema_token');
                if (!window.location.pathname.startsWith('/login')) {
                    window.location.href = '/login';
                }
            }
            return Promise.reject(error);
        }
    );

    return instance;
};

// Create service-specific axios instances
export const userApi = createAuthenticatedAxios(config.getServiceUrl('user'));
export const catalogApi = createAuthenticatedAxios(config.getServiceUrl('catalog'));
export const seatApi = createAuthenticatedAxios(config.getServiceUrl('seat'));
export const bookingApi = createAuthenticatedAxios(config.getServiceUrl('booking'));

// Default export for backwards compatibility (uses user service)
export default userApi;
