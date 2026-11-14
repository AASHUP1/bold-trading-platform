import type { Instrument, Candle, Timeframe } from './types';

export const INSTRUMENTS: Instrument[] = [
  // Stock Market (NSE/BSE)
  {
    symbol: 'NIFTY 50',
    name: 'NIFTY 50 Benchmark Index',
    market: 'stocks',
    price: 24360.50,
    change: 156.80,
    changePercent: 0.65,
    currency: '₹',
    high24h: 24410.00,
    low24h: 24205.20,
    volume24h: '3.42B',
    supportLevel: 24180.00,
    resistanceLevel: 24450.00,
  },
  {
    symbol: 'BANKNIFTY',
    name: 'NIFTY Bank Index',
    market: 'stocks',
    price: 51820.00,
    change: 218.40,
    changePercent: 0.42,
    currency: '₹',
    high24h: 52010.00,
    low24h: 51600.00,
    volume24h: '1.85B',
    supportLevel: 51500.00,
    resistanceLevel: 52100.00,
  },
  {
    symbol: 'RELIANCE',
    name: 'Reliance Industries Ltd',
    market: 'stocks',
    price: 2980.20,
    change: 33.80,
    changePercent: 1.15,
    currency: '₹',
    high24h: 2995.00,
    low24h: 2942.10,
    volume24h: '8.4M',
    supportLevel: 2930.00,
    resistanceLevel: 3010.00,
  },
  {
    symbol: 'HDFCBANK',
    name: 'HDFC Bank Ltd',
    market: 'stocks',
    price: 1640.75,
    change: -4.95,
    changePercent: -0.30,
    currency: '₹',
    high24h: 1655.00,
    low24h: 1632.50,
    volume24h: '12.1M',
    supportLevel: 1625.00,
    resistanceLevel: 1660.00,
  },
  {
    symbol: 'TATAMOTORS',
    name: 'Tata Motors Ltd',
    market: 'stocks',
    price: 940.10,
    change: 19.30,
    changePercent: 2.10,
    currency: '₹',
    high24h: 948.00,
    low24h: 918.50,
    volume24h: '15.6M',
    supportLevel: 910.00,
    resistanceLevel: 955.00,
  },

  // Crypto Trading
  {
    symbol: 'BTC/USDT',
    name: 'Bitcoin / Tether USD',
    market: 'crypto',
    price: 91450.00,
    change: 3390.00,
    changePercent: 3.85,
    currency: '$',
    high24h: 92100.00,
    low24h: 87800.00,
    volume24h: '42.8B',
    supportLevel: 88500.00,
    resistanceLevel: 93000.00,
  },
  {
    symbol: 'ETH/USDT',
    name: 'Ethereum / Tether USD',
    market: 'crypto',
    price: 3210.40,
    change: 67.50,
    changePercent: 2.15,
    currency: '$',
    high24h: 3280.00,
    low24h: 3120.00,
    volume24h: '18.2B',
    supportLevel: 3100.00,
    resistanceLevel: 3300.00,
  },
  {
    symbol: 'SOL/USDT',
    name: 'Solana / Tether USD',
    market: 'crypto',
    price: 218.60,
    change: 11.20,
    changePercent: 5.40,
    currency: '$',
    high24h: 224.50,
    low24h: 204.00,
    volume24h: '6.9B',
    supportLevel: 202.00,
    resistanceLevel: 226.00,
  },

  // Forex Trading
  {
    symbol: 'USD/INR',
    name: 'US Dollar / Indian Rupee',
    market: 'forex',
    price: 84.38,
    change: 0.04,
    changePercent: 0.05,
    currency: '₹',
    high24h: 84.42,
    low24h: 84.32,
    volume24h: '940M',
    supportLevel: 84.28,
    resistanceLevel: 84.45,
  },
  {
    symbol: 'EUR/USD',
    name: 'Euro / US Dollar',
    market: 'forex',
    price: 1.0840,
    change: -0.0013,
    changePercent: -0.12,
    currency: '$',
    high24h: 1.0875,
    low24h: 1.0815,
    volume24h: '1.2T',
    supportLevel: 1.0800,
    resistanceLevel: 1.0890,
  },
  {
    symbol: 'GBP/USD',
    name: 'British Pound / US Dollar',
    market: 'forex',
    price: 1.2965,
    change: 0.0028,
    changePercent: 0.22,
    currency: '$',
    high24h: 1.3010,
    low24h: 1.2920,
    volume24h: '850B',
    supportLevel: 1.2910,
    resistanceLevel: 1.3025,
  },
];

/**
 * Generates realistic candlestick data with genuine technical indicators
 */
export function generateCandles(instrument: Instrument, timeframe: Timeframe, count = 55): Candle[] {
  const basePrice = instrument.price;
  const isForex = instrument.market === 'forex';
  const volatility = isForex ? basePrice * 0.0015 : basePrice * 0.008;

  const candles: Candle[] = [];
  let currentClose = basePrice - volatility * 12;
  const now = Date.now();
  const stepMs = timeframe === '1m' ? 60000 : timeframe === '5m' ? 300000 : timeframe === '15m' ? 900000 : timeframe === '1H' ? 3600000 : 86400000;

  for (let i = count - 1; i >= 0; i--) {
    const timestamp = now - i * stepMs;
    const date = new Date(timestamp);
    const timeStr = date.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: false });

    // Seeded random walk
    const delta = (Math.sin(i * 0.45) * 0.7 + (Math.cos(i * 0.25) * 0.5) + (Math.random() - 0.48)) * volatility;
    const open = currentClose;
    const close = open + delta;
    const high = Math.max(open, close) + Math.random() * volatility * 0.6;
    const low = Math.min(open, close) - Math.random() * volatility * 0.6;
    const volume = Math.floor(15000 + Math.random() * 45000 * (Math.abs(delta) / volatility + 0.5));

    currentClose = close;

    candles.push({
      timestamp,
      timeStr,
      open: Number(open.toFixed(isForex ? 4 : 2)),
      high: Number(high.toFixed(isForex ? 4 : 2)),
      low: Number(low.toFixed(isForex ? 4 : 2)),
      close: Number(close.toFixed(isForex ? 4 : 2)),
      volume,
    });
  }

  // Calculate Moving Averages & RSI & MACD
  calculateIndicators(candles);

  return candles;
}

function calculateIndicators(candles: Candle[]) {
  // SMA 20
  for (let i = 0; i < candles.length; i++) {
    if (i >= 19) {
      let sum = 0;
      for (let j = i - 19; j <= i; j++) sum += candles[j].close;
      candles[i].sma20 = Number((sum / 20).toFixed(2));
    }
  }

  // EMA 20 & EMA 50
  const k20 = 2 / (20 + 1);
  const k50 = 2 / (50 + 1);
  let prevEma20 = candles[0].close;
  let prevEma50 = candles[0].close;

  for (let i = 0; i < candles.length; i++) {
    const close = candles[i].close;
    prevEma20 = i === 0 ? close : (close - prevEma20) * k20 + prevEma20;
    prevEma50 = i === 0 ? close : (close - prevEma50) * k50 + prevEma50;
    candles[i].ema20 = Number(prevEma20.toFixed(2));
    candles[i].ema50 = Number(prevEma50.toFixed(2));
  }

  // RSI 14
  for (let i = 0; i < candles.length; i++) {
    if (i >= 14) {
      let gains = 0;
      let losses = 0;
      for (let j = i - 13; j <= i; j++) {
        const diff = candles[j].close - candles[j - 1].close;
        if (diff >= 0) gains += diff;
        else losses += Math.abs(diff);
      }
      const avgGain = gains / 14;
      const avgLoss = losses / 14;
      if (avgLoss === 0) {
        candles[i].rsi = 100;
      } else {
        const rs = avgGain / avgLoss;
        candles[i].rsi = Number((100 - (100 / (1 + rs))).toFixed(1));
      }
    } else {
      candles[i].rsi = 52.4;
    }
  }

  // MACD (12, 26, 9)
  const k12 = 2 / (12 + 1);
  const k26 = 2 / (26 + 1);
  const k9 = 2 / (9 + 1);
  let ema12 = candles[0].close;
  let ema26 = candles[0].close;
  let macdSignal = 0;

  for (let i = 0; i < candles.length; i++) {
    const close = candles[i].close;
    ema12 = (close - ema12) * k12 + ema12;
    ema26 = (close - ema26) * k26 + ema26;
    const macdLine = ema12 - ema26;
    macdSignal = (macdLine - macdSignal) * k9 + macdSignal;
    const hist = macdLine - macdSignal;

    candles[i].macd = {
      macd: Number(macdLine.toFixed(2)),
      signal: Number(macdSignal.toFixed(2)),
      hist: Number(hist.toFixed(2)),
    };
  }
}