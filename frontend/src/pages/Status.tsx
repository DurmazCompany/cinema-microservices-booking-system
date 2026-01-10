import { useEffect, useState } from 'react';
import axios from 'axios';
import { config } from '../config';
import Loading from '../components/Loading';

interface ServiceStatus {
    name: string;
    url: string;
    status: 'UP' | 'DOWN' | 'UNKNOWN';
}

export default function Status() {
    const [statuses, setStatuses] = useState<ServiceStatus[]>([]);
    const [loading, setLoading] = useState(true);

    const services = [
        { name: 'User Service', url: config.getServiceUrl('user') },
        { name: 'Catalog Service', url: config.getServiceUrl('catalog') },
        { name: 'Seat Service', url: config.getServiceUrl('seat') },
        { name: 'Booking Service', url: config.getServiceUrl('booking') },
    ];

    useEffect(() => {
        checkStatuses();
    }, []);

    const checkStatuses = async () => {
        const results = await Promise.all(
            services.map(async (service) => {
                try {
                    const response = await axios.get(`${service.url}/actuator/health`);
                    return {
                        name: service.name,
                        url: service.url,
                        status: response.data.status === 'UP' ? 'UP' : 'DOWN',
                    } as ServiceStatus;
                } catch (error) {
                    return {
                        name: service.name,
                        url: service.url,
                        status: 'DOWN',
                    } as ServiceStatus;
                }
            })
        );
        setStatuses(results);
        setLoading(false);
    };

    if (loading) return <Loading />;

    return (
        <div className="max-w-4xl mx-auto">
            <h1 className="text-3xl font-bold text-gray-900 mb-8">🖥️ System Status</h1>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {statuses.map((service) => (
                    <div key={service.name} className="bg-white p-6 rounded-lg shadow flex justify-between items-center">
                        <div>
                            <h3 className="text-lg font-bold text-gray-900">{service.name}</h3>
                            <p className="text-sm text-gray-400">{service.url}</p>
                        </div>
                        <div>
                            <span className={`px-4 py-2 rounded-full font-bold text-white ${service.status === 'UP' ? 'bg-green-500' : 'bg-red-500'
                                }`}>
                                {service.status}
                            </span>
                        </div>
                    </div>
                ))}
            </div>

            <div className="mt-10 text-center">
                <button
                    onClick={() => { setLoading(true); checkStatuses(); }}
                    className="text-indigo-600 hover:text-indigo-800 font-medium"
                >
                    🔄 Refresh Status
                </button>
            </div>
        </div>
    );
}
