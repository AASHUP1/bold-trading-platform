export type MarketType = 'stocks' | 'crypto' | 'forex';

export type ChartType = 'candlestick' | 'line' | 'bar';

export type Timeframe = '1m' | '5m' | '15m' | '1H' | '4H' | '1D' | '1W';

export interface Candle {
  timestamp: number;
  timeStr: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
  ema20?: number;
  ema50?: number;
  sma20?: number;
  rsi?: number;
  macd?: {
    macd: number;
    signal: number;
    hist: number;
  };
}

export interface Instrument {
  symbol: string;
  name: string;
  market: MarketType;
  price: number;
  change: number;
  changePercent: number;
  currency: string;
  high24h: number;
  low24h: number;
  volume24h: string;
  supportLevel: number;
  resistanceLevel: number;
}

export interface ChartIndicators {
  ema20: boolean;
  ema50: boolean;
  sma20: boolean;
  rsi: boolean;
  macd: boolean;
  volume: boolean;
}

export interface DrawingTools {
  trendline: boolean;
  horizontalLevels: boolean;
}