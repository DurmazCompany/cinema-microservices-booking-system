import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { catalogApi } from '../api/axios';
import Loading from '../components/Loading';

interface Showtime {
    id: number;
    movieId: number;
    movieTitle?: string; // If backend provides it
    startTime: string;
    hallName: string;
    totalSeats: number;
    availableSeats: number; // calculated if possible, or mocked
}

export default function Showtimes() {
    const [showtimes, setShowtimes] = useState<Showtime[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetchShowtimes();
    }, []);

    const fetchShowtimes = async () => {
        try {
            const response = await catalogApi.get('/showtimes');
            console.log('API Response:', response); // Debug log
            if (Array.isArray(response.data)) {
                setShowtimes(response.data);
            } else {
                console.error('API returned non-array data:', response.data);
                setShowtimes([]);
                setError('Received invalid data format from server.');
            }
        } catch (err: any) {
            console.error('Fetch error:', err);
            const errorMessage = err.response?.status
                ? `Error ${err.response.status}: ${err.response.data?.message || err.message}`
                : `Network Error: ${err.message}. Check console.`;
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <Loading />;
    if (error) return <div className="text-center text-red-600 mt-10">{error}</div>;

    return (
        <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-8">🎬 Now Showing</h1>

            {showtimes.length === 0 ? (
                <p className="text-gray-500 text-center text-lg">No showtimes available.</p>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {showtimes.map((showtime) => (
                        <div key={showtime.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
                            <div className="p-6">
                                <div className="flex justify-between items-start mb-4">
                                    <div>
                                        <span className="inline-block px-2 py-1 text-xs font-semibold bg-indigo-100 text-indigo-800 rounded-full mb-2">
                                            {showtime.hallName}
                                        </span>
                                        <h3 className="text-xl font-bold text-gray-800">Movie #{showtime.movieId}</h3> {/* Title ideally comes from Movie entity */}
                                    </div>
                                </div>

                                <div className="space-y-3 mb-6">
                                    <div className="flex items-center text-gray-600">
                                        📅 {new Date(showtime.startTime).toLocaleString()}
                                    </div>
                                    <div className="flex items-center text-gray-600">
                                        🪑 {showtime.totalSeats} Total Seats
                                    </div>
                                </div>

                                <button
                                    onClick={() => navigate(`/showtimes/${showtime.id}/seats`)}
                                    className="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 transition-colors font-medium"
                                >
                                    Select Seats
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
