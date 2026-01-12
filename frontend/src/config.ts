export const config = {
    services: {
        user: import.meta.env.VITE_USER_SERVICE_URL || 'http://localhost:8085',
        catalog: import.meta.env.VITE_CATALOG_SERVICE_URL || 'http://localhost:8082',
        seat: import.meta.env.VITE_SEAT_SERVICE_URL || 'http://localhost:8083',
        booking: import.meta.env.VITE_BOOKING_SERVICE_URL || 'http://localhost:8084'
    },

    getServiceUrl: (serviceName: 'user' | 'catalog' | 'seat' | 'booking') => {
        return config.services[serviceName];
    }
};
