export const config = {
    services: {
        // Hardcoded Render URLs to ensure absolute paths are used
        user: 'https://user-service-zcw6.onrender.com',
        catalog: 'https://catalog-service-msei.onrender.com',
        seat: 'https://seat-service-1qck.onrender.com',
        booking: 'https://booking-service-lhk5.onrender.com'
    },

    getServiceUrl: (serviceName: 'user' | 'catalog' | 'seat' | 'booking') => {
        return config.services[serviceName];
    }
};
