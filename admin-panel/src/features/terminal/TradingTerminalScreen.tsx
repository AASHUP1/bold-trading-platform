import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  TrendingUp, 
  BarChart2, 
  Activity, 
  Search, 
  ZoomIn, 
  ZoomOut, 
  RotateCcw, 
  Layers, 
  Edit3, 
  Clock, 
  ArrowUpRight, 
  ArrowDownRight 
} from 'lucide-react';
import { TradingChart } from './TradingChart';
import { INSTRUMENTS, generateCandles } from './mockData';
import type { 
  MarketType, 
  ChartType, 
  Timeframe, 
  ChartIndicators, 
  DrawingTools, 
  Candle 
} from './types';

export const TradingTerminalScreen: React.FC = () => {
  const [selectedMarket, setSelectedMarket] = useState<MarketType>('stocks');
  const [selectedSymbol, setSelectedSymbol] = useState<string>('NIFTY 50');
  const [chartType, setChartType] = useState<ChartType>('candlestick');
  const [timeframe, setTimeframe] = useState<Timeframe>('15m');
  const [zoomLevel, setZoomLevel] = useState<number>(1);
  const [searchQuery, setSearchQuery] = useState('');
  
  // Indicators & Tools toggles
  const [indicators, setIndicators] = useState<ChartIndicators>({
    ema20: true,
    ema50: true,
    sma20: false,
    rsi: true,
    macd: false,
    volume: true,
  });

  const [drawingTools, setDrawingTools] = useState<DrawingTools>({
    trendline: true,
    horizontalLevels: true,
  });

  // Instruments filtered by market and search
  const marketInstruments = INSTRUMENTS.filter(inst => inst.market === selectedMarket);
  const filteredInstruments = marketInstruments.filter(inst => 
    inst.symbol.toLowerCase().includes(searchQuery.toLowerCase()) ||
    inst.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const activeInstrument = INSTRUMENTS.find(inst => inst.symbol === selectedSymbol) || INSTRUMENTS[0];

  // Candles data state
  const [candles, setCandles] = useState<Candle[]>(() => generateCandles(activeInstrument, timeframe));

  // Regenerate candles when symbol or timeframe changes
  useEffect(() => {
    setCandles(generateCandles(activeInstrument, timeframe));
  }, [activeInstrument.symbol, timeframe]);

  // When changing market, pick the first instrument in that market
  const handleMarketChange = (market: MarketType) => {
    setSelectedMarket(market);
    const firstInMarket = INSTRUMENTS.find(inst => inst.market === market);
    if (firstInMarket) {
      setSelectedSymbol(firstInMarket.symbol);
    }
  };

  // Live Simulated Price Tick Engine
  useEffect(() => {
    const interval = setInterval(() => {
      setCandles(prev => {
        if (prev.length === 0) return prev;
        const last = { ...prev[prev.length - 1] };
        const tickDelta = (Math.random() - 0.49) * (activeInstrument.market === 'forex' ? 0.0008 : activeInstrument.price * 0.0006);
        const newClose = Number((last.close + tickDelta).toFixed(activeInstrument.market === 'forex' ? 4 : 2));
        last.close = newClose;
        last.high = Math.max(last.high, newClose);
        last.low = Math.min(last.low, newClose);
        last.volume += Math.floor(Math.random() * 85);
        return [...prev.slice(0, -1), last];
      });
    }, 1500);

    return () => clearInterval(interval);
  }, [activeInstrument]);

  return (
    <div className="space-y-4 max-w-7xl mx-auto">
      {/* Top Bar: Market Selector & Live Stats */}
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-3 bg-surface-card border border-border-subtle p-3 sm:p-4 rounded-xl shadow-sm">
        {/* Market Category Selector */}
        <div className="flex flex-wrap items-center gap-1.5">
          <span className="text-xs font-semibold text-text-secondary mr-1">Market:</span>
          <button
            type="button"
            onClick={() => handleMarketChange('stocks')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
              selectedMarket === 'stocks'
                ? 'bg-primary-bullish text-bg-app shadow-glow-green font-bold'
                : 'bg-surface-elevated text-text-secondary hover:text-text-primary border border-border-subtle'
            }`}
          >
            🇮🇳 Stock Market (NSE/BSE)
          </button>
          <button
            type="button"
            onClick={() => handleMarketChange('crypto')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
              selectedMarket === 'crypto'
                ? 'bg-primary-accent text-bg-app shadow-glow-gold font-bold'
                : 'bg-surface-elevated text-text-secondary hover:text-text-primary border border-border-subtle'
            }`}
          >
            ₿ Crypto Trading
          </button>
          <button
            type="button"
            onClick={() => handleMarketChange('forex')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
              selectedMarket === 'forex'
                ? 'bg-[#00E5FF] text-bg-app font-bold'
                : 'bg-surface-elevated text-text-secondary hover:text-text-primary border border-border-subtle'
            }`}
          >
            💱 Forex Trading
          </button>
        </div>

        {/* Active Instrument 24h Telemetry Strip */}
        <div className="flex flex-wrap items-center gap-3 sm:gap-6 text-xs font-mono">
          <div>
            <span className="text-[10px] text-text-muted block">Last Price</span>
            <span className="text-base font-bold text-text-primary">
              {activeInstrument.currency}{activeInstrument.price.toLocaleString()}
            </span>
          </div>

          <div>
            <span className="text-[10px] text-text-muted block">24h Change</span>
            <span className={`font-semibold flex items-center ${activeInstrument.change >= 0 ? 'text-primary-bullish' : 'text-accent-bearish'}`}>
              {activeInstrument.change >= 0 ? <ArrowUpRight className="h-3.5 w-3.5 mr-0.5" /> : <ArrowDownRight className="h-3.5 w-3.5 mr-0.5" />}
              {activeInstrument.change >= 0 ? '+' : ''}{activeInstrument.changePercent}%
            </span>
          </div>

          <div className="hidden sm:block">
            <span className="text-[10px] text-text-muted block">24h Range</span>
            <span className="text-text-secondary">
              {activeInstrument.low24h} - {activeInstrument.high24h}
            </span>
          </div>

          <div className="hidden md:block">
            <span className="text-[10px] text-text-muted block">24h Volume</span>
            <span className="text-text-secondary">{activeInstrument.volume24h}</span>
          </div>

          <Badge variant="healthy" dot>Live Feed</Badge>
        </div>
      </div>

      {/* Main Grid: Watchlist on Left, Chart Terminal on Right */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-4">
        {/* Left: Watchlist Panel */}
        <Card className="lg:col-span-1 p-0 flex flex-col h-[640px]">
          <div className="p-3 border-b border-border-subtle space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-text-primary uppercase tracking-wider flex items-center gap-1.5">
                <Activity className="h-3.5 w-3.5 text-primary-bullish" />
                Watchlist
              </span>
              <span className="text-[11px] font-mono text-text-muted">{filteredInstruments.length} Pairs</span>
            </div>

            <div className="relative">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-text-muted" />
              <input
                type="text"
                placeholder="Search symbol..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full bg-surface-elevated border border-border-subtle rounded-md pl-8 pr-2.5 py-1 text-xs text-text-primary placeholder:text-text-muted focus:outline-none focus:ring-1 focus:ring-primary-bullish"
              />
            </div>
          </div>

          <div className="flex-1 overflow-y-auto divide-y divide-border-subtle/40">
            {filteredInstruments.map((inst) => {
              const isSelected = inst.symbol === selectedSymbol;
              const isBull = inst.change >= 0;
              return (
                <div
                  key={inst.symbol}
                  onClick={() => setSelectedSymbol(inst.symbol)}
                  className={`p-3 cursor-pointer transition-colors flex items-center justify-between hover:bg-surface-elevated/60 ${
                    isSelected ? 'bg-surface-elevated border-l-2 border-primary-bullish' : ''
                  }`}
                >
                  <div className="min-w-0">
                    <p className="text-xs font-bold text-text-primary truncate">{inst.symbol}</p>
                    <p className="text-[10px] text-text-muted truncate">{inst.name}</p>
                  </div>
                  <div className="text-right font-mono">
                    <p className="text-xs font-bold text-text-primary">
                      {inst.currency}{inst.price.toLocaleString()}
                    </p>
                    <p className={`text-[10px] font-semibold ${isBull ? 'text-primary-bullish' : 'text-accent-bearish'}`}>
                      {isBull ? '+' : ''}{inst.changePercent}%
                    </p>
                  </div>
                </div>
              );
            })}
          </div>

          <div className="p-2.5 border-t border-border-subtle bg-surface-elevated/40 text-[10px] text-text-muted flex items-center justify-between">
            <span>Tick Frequency: 1500ms</span>
            <span className="text-primary-bullish font-mono">WebSocket Connected</span>
          </div>
        </Card>

        {/* Right: Chart Terminal & Controls */}
        <Card className="lg:col-span-3 p-0 flex flex-col h-[640px] overflow-hidden">
          {/* Chart Header & Action Toolbar */}
          <div className="p-3 border-b border-border-subtle flex flex-wrap items-center justify-between gap-2.5 bg-surface-card">
            {/* Chart Type Selector */}
            <div className="flex items-center gap-1 bg-surface-elevated p-1 rounded-lg border border-border-subtle">
              <button
                type="button"
                onClick={() => setChartType('candlestick')}
                className={`px-2.5 py-1 text-xs rounded font-medium transition-colors flex items-center gap-1 ${
                  chartType === 'candlestick'
                    ? 'bg-primary-bullish text-bg-app font-bold'
                    : 'text-text-secondary hover:text-text-primary'
                }`}
                title="Candlestick Chart"
              >
                <TrendingUp className="h-3.5 w-3.5" />
                <span className="hidden sm:inline">Candles</span>
              </button>
              <button
                type="button"
                onClick={() => setChartType('line')}
                className={`px-2.5 py-1 text-xs rounded font-medium transition-colors flex items-center gap-1 ${
                  chartType === 'line'
                    ? 'bg-primary-bullish text-bg-app font-bold'
                    : 'text-text-secondary hover:text-text-primary'
                }`}
                title="Line Area Chart"
              >
                <Activity className="h-3.5 w-3.5" />
                <span className="hidden sm:inline">Line</span>
              </button>
              <button
                type="button"
                onClick={() => setChartType('bar')}
                className={`px-2.5 py-1 text-xs rounded font-medium transition-colors flex items-center gap-1 ${
                  chartType === 'bar'
                    ? 'bg-primary-bullish text-bg-app font-bold'
                    : 'text-text-secondary hover:text-text-primary'
                }`}
                title="OHLC Bar Chart"
              >
                <BarChart2 className="h-3.5 w-3.5" />
                <span className="hidden sm:inline">Bar</span>
              </button>
            </div>

            {/* Timeframes Selector */}
            <div className="flex items-center gap-1 bg-surface-elevated p-1 rounded-lg border border-border-subtle">
              <Clock className="h-3.5 w-3.5 text-text-muted ml-1 hidden xs:block" />
              {(['1m', '5m', '15m', '1H', '4H', '1D', '1W'] as Timeframe[]).map((tf) => (
                <button
                  key={tf}
                  type="button"
                  onClick={() => setTimeframe(tf)}
                  className={`px-2 py-0.5 text-xs font-mono rounded transition-colors ${
                    timeframe === tf
                      ? 'bg-primary-bullish text-bg-app font-bold'
                      : 'text-text-secondary hover:text-text-primary'
                  }`}
                >
                  {tf}
                </button>
              ))}
            </div>

            {/* Indicators & Drawing Tools & Zoom */}
            <div className="flex items-center gap-1.5">
              {/* Technical Indicators Pill Menu */}
              <div className="flex items-center gap-1 bg-surface-elevated p-1 rounded-lg border border-border-subtle">
                <span className="text-[11px] text-text-muted px-1 hidden md:flex items-center gap-1">
                  <Layers className="h-3 w-3" /> Indicators:
                </span>
                <button
                  type="button"
                  onClick={() => setIndicators(prev => ({ ...prev, ema20: !prev.ema20 }))}
                  className={`px-2 py-0.5 text-[11px] rounded font-mono transition-colors ${
                    indicators.ema20 ? 'bg-[#00E5FF]/20 text-[#00E5FF] font-bold border border-[#00E5FF]/40' : 'text-text-muted'
                  }`}
                >
                  EMA20
                </button>
                <button
                  type="button"
                  onClick={() => setIndicators(prev => ({ ...prev, ema50: !prev.ema50 }))}
                  className={`px-2 py-0.5 text-[11px] rounded font-mono transition-colors ${
                    indicators.ema50 ? 'bg-[#F3C969]/20 text-[#F3C969] font-bold border border-[#F3C969]/40' : 'text-text-muted'
                  }`}
                >
                  EMA50
                </button>
                <button
                  type="button"
                  onClick={() => setIndicators(prev => ({ ...prev, rsi: !prev.rsi }))}
                  className={`px-2 py-0.5 text-[11px] rounded font-mono transition-colors ${
                    indicators.rsi ? 'bg-[#D500F9]/20 text-[#D500F9] font-bold border border-[#D500F9]/40' : 'text-text-muted'
                  }`}
                >
                  RSI
                </button>
                <button
                  type="button"
                  onClick={() => setIndicators(prev => ({ ...prev, volume: !prev.volume }))}
                  className={`px-2 py-0.5 text-[11px] rounded font-mono transition-colors ${
                    indicators.volume ? 'bg-primary-bullish/20 text-primary-bullish font-bold border border-primary-bullish/40' : 'text-text-muted'
                  }`}
                >
                  VOL
                </button>
              </div>

              {/* Drawing Tools (S/R Levels & Trendline) */}
              <div className="flex items-center gap-1 bg-surface-elevated p-1 rounded-lg border border-border-subtle">
                <button
                  type="button"
                  onClick={() => setDrawingTools(prev => ({ ...prev, horizontalLevels: !prev.horizontalLevels }))}
                  className={`px-2 py-0.5 text-[11px] rounded font-medium transition-colors flex items-center gap-1 ${
                    drawingTools.horizontalLevels
                      ? 'bg-primary-bullish/20 text-primary-bullish font-semibold border border-primary-bullish/30'
                      : 'text-text-muted'
                  }`}
                  title="Toggle Support & Resistance Horizontal Levels"
                >
                  <Edit3 className="h-3 w-3" />
                  <span className="hidden xl:inline">S/R Levels</span>
                </button>
                <button
                  type="button"
                  onClick={() => setDrawingTools(prev => ({ ...prev, trendline: !prev.trendline }))}
                  className={`px-2 py-0.5 text-[11px] rounded font-medium transition-colors flex items-center gap-1 ${
                    drawingTools.trendline
                      ? 'bg-[#F3C969]/20 text-[#F3C969] font-semibold border border-[#F3C969]/30'
                      : 'text-text-muted'
                  }`}
                  title="Toggle Trendline Overlay"
                >
                  <span className="hidden xl:inline">Trendline</span>
                </button>
              </div>

              {/* Zoom Controls */}
              <div className="flex items-center gap-0.5 bg-surface-elevated p-1 rounded-lg border border-border-subtle">
                <button
                  type="button"
                  onClick={() => setZoomLevel(prev => Math.min(2.0, prev + 0.25))}
                  className="p-1 text-text-secondary hover:text-text-primary"
                  title="Zoom In"
                >
                  <ZoomIn className="h-3.5 w-3.5" />
                </button>
                <button
                  type="button"
                  onClick={() => setZoomLevel(prev => Math.max(0.6, prev - 0.25))}
                  className="p-1 text-text-secondary hover:text-text-primary"
                  title="Zoom Out"
                >
                  <ZoomOut className="h-3.5 w-3.5" />
                </button>
                <button
                  type="button"
                  onClick={() => setZoomLevel(1)}
                  className="p-1 text-text-secondary hover:text-text-primary"
                  title="Reset Zoom"
                >
                  <RotateCcw className="h-3.5 w-3.5" />
                </button>
              </div>
            </div>
          </div>

          {/* Interactive Chart Canvas Area */}
          <div className="flex-1 bg-bg-app overflow-hidden">
            <TradingChart
              candles={candles}
              instrument={activeInstrument}
              chartType={chartType}
              indicators={indicators}
              drawingTools={drawingTools}
              zoomLevel={zoomLevel}
            />
          </div>

          {/* Bottom Bar: Key Levels & Hotkeys */}
          <div className="px-4 py-2 border-t border-border-subtle bg-surface-card flex flex-wrap items-center justify-between text-[11px] font-mono text-text-secondary">
            <div className="flex items-center gap-4">
              <span className="flex items-center gap-1">
                <span className="h-2 w-2 rounded-full bg-[#FF657A]"></span>
                Resistance: <span className="text-text-primary font-bold">{activeInstrument.currency}{activeInstrument.resistanceLevel}</span>
              </span>
              <span className="flex items-center gap-1">
                <span className="h-2 w-2 rounded-full bg-[#38D996]"></span>
                Support: <span className="text-text-primary font-bold">{activeInstrument.currency}{activeInstrument.supportLevel}</span>
              </span>
            </div>

            <div className="flex items-center gap-3 text-text-muted">
              <span>● Interactive Crosshair Enabled</span>
              <span>● Auto-Scale Y-Axis</span>
              <span>100% Free Virtual Feed</span>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};