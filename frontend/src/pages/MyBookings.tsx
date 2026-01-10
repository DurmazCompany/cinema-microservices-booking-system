import { useEffect, useState } from 'react';
import { config } from '../config';
import api from '../api/axios';
import Loading from '../components/Loading';

interface Booking {
    id: number;
    showtimeId: number;
    seatNumber: number;
    status: string;
    bookingTime: string;
}

export default function MyBookings() {
    const [bookings, setBookings] = useState<Booking[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchBookings();
    }, []);

    const fetchBookings = async () => {
        try {
            const url = `${config.getServiceUrl('booking')}/bookings/me`;
            const response = await api.get(url);
            setBookings(response.data);
        } catch (err) {
            setError('Failed to load bookings.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <Loading />;

    return (
        <div className="max-w-4xl mx-auto">
            <h1 className="text-3xl font-bold text-gray-900 mb-8">🎟️ My Bookings</h1>

            {error && <div className="bg-red-50 text-red-600 p-4 rounded mb-6">{error}</div>}

            {bookings.length === 0 ? (
                <div className="text-center py-12 bg-white rounded-lg shadow">
                    <p className="text-gray-500 text-lg">No bookings found yet.</p>
                </div>
            ) : (
                <div className="space-y-4">
                    {bookings.map((booking) => (
                        <div key={booking.id} className="bg-white p-6 rounded-lg shadow border-l-4 border-indigo-500 flex justify-between items-center">
                            <div>
                                <p className="text-sm text-gray-500">Booking ID: #{booking.id}</p>
                                <div className="mt-1">
                                    <span className="text-xl font-bold text-gray-900">Showtime #{booking.showtimeId}</span>
                                    <span className="mx-2 text-gray-300">|</span>
                                    <span className="text-lg text-indigo-600 font-medium">Seat {booking.seatNumber}</span>
                                </div>
                                <p className="text-xs text-gray-400 mt-2">Booked on: {new Date(booking.bookingTime).toLocaleString()}</p>
                            </div>
                            <div>
                                <span className={`px-3 py-1 rounded-full text-sm font-bold ${booking.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                                    }`}>
                                    {booking.status}
                                </span>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
