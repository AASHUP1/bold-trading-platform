import React, { useState, useRef } from 'react';
import type { Candle, ChartType, ChartIndicators, DrawingTools, Instrument } from './types';

interface TradingChartProps {
  candles: Candle[];
  instrument: Instrument;
  chartType: ChartType;
  indicators: ChartIndicators;
  drawingTools: DrawingTools;
  zoomLevel: number;
}

export const TradingChart: React.FC<TradingChartProps> = ({
  candles,
  instrument,
  chartType,
  indicators,
  drawingTools,
  zoomLevel,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [hoverIndex, setHoverIndex] = useState<number | null>(null);
  const [crosshairPos, setCrosshairPos] = useState<{ x: number; y: number } | null>(null);

  // Slice based on zoom level (default 45 candles, zoomed in 25, zoomed out 60)
  const visibleCount = Math.max(15, Math.min(candles.length, Math.round(45 / zoomLevel)));
  const visibleCandles = candles.slice(-visibleCount);

  // Layout measurements
  const width = 900;
  const height = 480;
  const paddingRight = 75; // for price axis
  const paddingBottom = 30; // for time axis
  const subPanelHeight = indicators.rsi || indicators.macd ? 110 : 0;
  const mainHeight = height - paddingBottom - subPanelHeight - 20;

  // Min and max price calculation
  const allPrices: number[] = [];
  visibleCandles.forEach(c => {
    allPrices.push(c.high, c.low);
    if (indicators.ema20 && c.ema20) allPrices.push(c.ema20);
    if (indicators.ema50 && c.ema50) allPrices.push(c.ema50);
    if (indicators.sma20 && c.sma20) allPrices.push(c.sma20);
  });
  if (drawingTools.horizontalLevels) {
    allPrices.push(instrument.supportLevel, instrument.resistanceLevel);
  }

  const minPrice = Math.min(...allPrices);
  const maxPrice = Math.max(...allPrices);
  const priceRange = maxPrice - minPrice || 1;
  const paddingPrice = priceRange * 0.08;
  const effectiveMin = minPrice - paddingPrice;
  const effectiveMax = maxPrice + paddingPrice;
  const effectiveRange = effectiveMax - effectiveMin;

  // Max volume for volume bars
  const maxVolume = Math.max(...visibleCandles.map(c => c.volume), 1);

  // Coordinate mappers
  const chartWidth = width - paddingRight;
  const candleStep = chartWidth / visibleCandles.length;
  const candleWidth = Math.max(2, candleStep * 0.7);

  const getX = (idx: number) => idx * candleStep + candleStep / 2;
  const getY = (price: number) => mainHeight - ((price - effectiveMin) / effectiveRange) * mainHeight + 10;
  const getPriceFromY = (y: number) => {
    const ratio = (mainHeight + 10 - y) / mainHeight;
    return effectiveMin + ratio * effectiveRange;
  };

  // Active hover candle (or latest candle by default)
  const activeCandle = hoverIndex !== null && visibleCandles[hoverIndex]
    ? visibleCandles[hoverIndex]
    : visibleCandles[visibleCandles.length - 1];

  const handleMouseMove = (e: React.MouseEvent<SVGSVGElement>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const clientX = e.clientX - rect.left;
    const clientY = e.clientY - rect.top;
    
    // Convert to SVG viewBox coordinate system
    const scaleX = width / rect.width;
    const scaleY = height / rect.height;
    const svgX = clientX * scaleX;
    const svgY = clientY * scaleY;

    if (svgX >= 0 && svgX <= chartWidth && svgY >= 0 && svgY <= height - paddingBottom) {
      setCrosshairPos({ x: svgX, y: svgY });
      const idx = Math.floor(svgX / candleStep);
      setHoverIndex(Math.max(0, Math.min(visibleCandles.length - 1, idx)));
    } else {
      setCrosshairPos(null);
      setHoverIndex(null);
    }
  };

  const handleMouseLeave = () => {
    setCrosshairPos(null);
    setHoverIndex(null);
  };

  // Generate paths for Moving Averages
  const generateLinePath = (getter: (c: Candle) => number | undefined) => {
    let path = '';
    visibleCandles.forEach((c, i) => {
      const val = getter(c);
      if (val !== undefined) {
        const x = getX(i);
        const y = getY(val);
        path += path === '' ? `M ${x} ${y}` : ` L ${x} ${y}`;
      }
    });
    return path;
  };

  // Price Grid Lines (5 horizontal ticks)
  const priceGridTicks = [0, 0.25, 0.5, 0.75, 1].map(r => effectiveMin + r * effectiveRange);

  return (
    <div ref={containerRef} className="relative w-full overflow-hidden select-none">
      {/* Top Floating Status / OHLC Bar */}
      {activeCandle && (
        <div className="flex flex-wrap items-center gap-x-4 gap-y-1 px-4 py-2 bg-surface-card/90 border-b border-border-subtle text-xs font-mono">
          <span className="font-bold font-sans text-text-primary text-sm flex items-center gap-1.5">
            {instrument.symbol}
            <span className="text-[11px] font-mono text-text-muted">({activeCandle.timeStr})</span>
          </span>
          <span className="text-text-muted">
            O: <span className="text-text-primary">{activeCandle.open.toFixed(instrument.market === 'forex' ? 4 : 2)}</span>
          </span>
          <span className="text-text-muted">
            H: <span className="text-primary-bullish">{activeCandle.high.toFixed(instrument.market === 'forex' ? 4 : 2)}</span>
          </span>
          <span className="text-text-muted">
            L: <span className="text-accent-bearish">{activeCandle.low.toFixed(instrument.market === 'forex' ? 4 : 2)}</span>
          </span>
          <span className="text-text-muted">
            C: <span className={activeCandle.close >= activeCandle.open ? 'text-primary-bullish font-bold' : 'text-accent-bearish font-bold'}>
              {activeCandle.close.toFixed(instrument.market === 'forex' ? 4 : 2)}
            </span>
          </span>
          <span className="text-text-muted">
            Vol: <span className="text-text-primary">{activeCandle.volume.toLocaleString()}</span>
          </span>
          {indicators.ema20 && activeCandle.ema20 && (
            <span className="text-[#00E5FF]">EMA(20): {activeCandle.ema20}</span>
          )}
          {indicators.ema50 && activeCandle.ema50 && (
            <span className="text-[#F3C969]">EMA(50): {activeCandle.ema50}</span>
          )}
          {indicators.sma20 && activeCandle.sma20 && (
            <span className="text-[#B388FF]">SMA(20): {activeCandle.sma20}</span>
          )}
          {indicators.rsi && activeCandle.rsi && (
            <span className="text-[#D500F9]">RSI(14): {activeCandle.rsi}</span>
          )}
        </div>
      )}

      {/* Main SVG Render Engine */}
      <svg
        viewBox={`0 0 ${width} ${height}`}
        className="w-full h-auto cursor-crosshair bg-bg-app"
        onMouseMove={handleMouseMove}
        onMouseLeave={handleMouseLeave}
      >
        <defs>
          <linearGradient id="areaGradient" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#38D996" stopOpacity="0.35" />
            <stop offset="100%" stopColor="#38D996" stopOpacity="0.0" />
          </linearGradient>
          <clipPath id="chartAreaClip">
            <rect x="0" y="0" width={chartWidth} height={mainHeight + 15} />
          </clipPath>
        </defs>

        {/* Horizontal Price Grid Lines */}
        {priceGridTicks.map((p, idx) => {
          const y = getY(p);
          return (
            <g key={idx}>
              <line
                x1={0}
                y1={y}
                x2={chartWidth}
                y2={y}
                stroke="#1C2734"
                strokeDasharray="3 3"
                strokeWidth={1}
              />
              <text
                x={chartWidth + 8}
                y={y + 3.5}
                fill="#7E8B9B"
                fontSize={10}
                fontFamily="monospace"
              >
                {instrument.currency}{p.toFixed(instrument.market === 'forex' ? 4 : 2)}
              </text>
            </g>
          );
        })}

        {/* Vertical Time Grid Lines */}
        {visibleCandles.filter((_, i) => i % Math.ceil(visibleCandles.length / 6) === 0).map((c, i) => {
          const originalIdx = visibleCandles.indexOf(c);
          const x = getX(originalIdx);
          return (
            <g key={i}>
              <line
                x1={x}
                y1={0}
                x2={x}
                y2={mainHeight + 10}
                stroke="#1C2734"
                strokeDasharray="2 4"
                strokeWidth={1}
              />
              <text
                x={x}
                y={mainHeight + 24}
                textAnchor="middle"
                fill="#7E8B9B"
                fontSize={10}
                fontFamily="monospace"
              >
                {c.timeStr}
              </text>
            </g>
          );
        })}

        {/* Volume Histogram (Bottom 25% of Main Chart) */}
        {indicators.volume && (
          <g opacity={0.45}>
            {visibleCandles.map((c, idx) => {
              const x = getX(idx);
              const barHeight = (c.volume / maxVolume) * (mainHeight * 0.22);
              const y = mainHeight + 10 - barHeight;
              const isBull = c.close >= c.open;
              return (
                <rect
                  key={idx}
                  x={x - candleWidth / 2}
                  y={y}
                  width={candleWidth}
                  height={barHeight}
                  fill={isBull ? '#38D996' : '#FF657A'}
                />
              );
            })}
          </g>
        )}

        {/* Moving Averages (EMA 20, EMA 50, SMA 20) */}
        <g clipPath="url(#chartAreaClip)">
          {indicators.ema20 && (
            <path
              d={generateLinePath(c => c.ema20)}
              fill="none"
              stroke="#00E5FF"
              strokeWidth={1.5}
            />
          )}
          {indicators.ema50 && (
            <path
              d={generateLinePath(c => c.ema50)}
              fill="none"
              stroke="#F3C969"
              strokeWidth={1.5}
            />
          )}
          {indicators.sma20 && (
            <path
              d={generateLinePath(c => c.sma20)}
              fill="none"
              stroke="#B388FF"
              strokeWidth={1.5}
            />
          )}
        </g>

        {/* Primary Price Series */}
        <g clipPath="url(#chartAreaClip)">
          {/* 1. Candlestick Mode */}
          {chartType === 'candlestick' &&
            visibleCandles.map((c, idx) => {
              const x = getX(idx);
              const isBull = c.close >= c.open;
              const color = isBull ? '#38D996' : '#FF657A';
              const topY = getY(Math.max(c.open, c.close));
              const botY = getY(Math.min(c.open, c.close));
              const bodyHeight = Math.max(1.5, botY - topY);

              return (
                <g key={idx}>
                  {/* High - Low Wick */}
                  <line
                    x1={x}
                    y1={getY(c.high)}
                    x2={x}
                    y2={getY(c.low)}
                    stroke={color}
                    strokeWidth={1.2}
                  />
                  {/* Candle Body */}
                  <rect
                    x={x - candleWidth / 2}
                    y={topY}
                    width={candleWidth}
                    height={bodyHeight}
                    fill={color}
                    rx={1}
                  />
                </g>
              );
            })}

          {/* 2. Line Chart Mode */}
          {chartType === 'line' && (
            <>
              {/* Area Under Curve */}
              <path
                d={`${generateLinePath(c => c.close)} L ${getX(visibleCandles.length - 1)} ${mainHeight + 10} L ${getX(0)} ${mainHeight + 10} Z`}
                fill="url(#areaGradient)"
              />
              {/* Glowing Line */}
              <path
                d={generateLinePath(c => c.close)}
                fill="none"
                stroke="#38D996"
                strokeWidth={2}
              />
            </>
          )}

          {/* 3. Bar Chart (OHLC) Mode */}
          {chartType === 'bar' &&
            visibleCandles.map((c, idx) => {
              const x = getX(idx);
              const isBull = c.close >= c.open;
              const color = isBull ? '#38D996' : '#FF657A';
              const openY = getY(c.open);
              const closeY = getY(c.close);
              const tickLen = candleWidth * 0.6;

              return (
                <g key={idx}>
                  {/* Central Spine */}
                  <line
                    x1={x}
                    y1={getY(c.high)}
                    x2={x}
                    y2={getY(c.low)}
                    stroke={color}
                    strokeWidth={1.5}
                  />
                  {/* Left Open Tick */}
                  <line
                    x1={x - tickLen}
                    y1={openY}
                    x2={x}
                    y2={openY}
                    stroke={color}
                    strokeWidth={1.5}
                  />
                  {/* Right Close Tick */}
                  <line
                    x1={x}
                    y1={closeY}
                    x2={x + tickLen}
                    y2={closeY}
                    stroke={color}
                    strokeWidth={1.5}
                  />
                </g>
              );
            })}
        </g>

        {/* Support & Resistance Drawing Overlays */}
        {drawingTools.horizontalLevels && (
          <g>
            {/* Resistance Line */}
            <line
              x1={0}
              y1={getY(instrument.resistanceLevel)}
              x2={chartWidth}
              y2={getY(instrument.resistanceLevel)}
              stroke="#FF657A"
              strokeWidth={1.5}
              strokeDasharray="4 4"
            />
            <rect
              x={chartWidth + 4}
              y={getY(instrument.resistanceLevel) - 8}
              width={66}
              height={16}
              fill="#FF657A"
              rx={2}
            />
            <text
              x={chartWidth + 7}
              y={getY(instrument.resistanceLevel) + 3.5}
              fill="#0B0F14"
              fontSize={9}
              fontWeight="bold"
              fontFamily="monospace"
            >
              RES {instrument.resistanceLevel}
            </text>

            {/* Support Line */}
            <line
              x1={0}
              y1={getY(instrument.supportLevel)}
              x2={chartWidth}
              y2={getY(instrument.supportLevel)}
              stroke="#38D996"
              strokeWidth={1.5}
              strokeDasharray="4 4"
            />
            <rect
              x={chartWidth + 4}
              y={getY(instrument.supportLevel) - 8}
              width={66}
              height={16}
              fill="#38D996"
              rx={2}
            />
            <text
              x={chartWidth + 7}
              y={getY(instrument.supportLevel) + 3.5}
              fill="#0B0F14"
              fontSize={9}
              fontWeight="bold"
              fontFamily="monospace"
            >
              SUP {instrument.supportLevel}
            </text>
          </g>
        )}

        {/* Dynamic Trendline Overlay */}
        {drawingTools.trendline && (
          <g>
            <line
              x1={getX(2)}
              y1={getY(visibleCandles[2]?.low || minPrice)}
              x2={getX(visibleCandles.length - 3)}
              y2={getY(visibleCandles[visibleCandles.length - 3]?.low || minPrice)}
              stroke="#F3C969"
              strokeWidth={2}
              strokeDasharray="5 3"
            />
            <circle cx={getX(2)} cy={getY(visibleCandles[2]?.low || minPrice)} r={3} fill="#F3C969" />
            <circle cx={getX(visibleCandles.length - 3)} cy={getY(visibleCandles[visibleCandles.length - 3]?.low || minPrice)} r={3} fill="#F3C969" />
          </g>
        )}

        {/* Sub-Panel: RSI (14) or MACD (12, 26, 9) */}
        {indicators.rsi && (
          <g transform={`translate(0, ${mainHeight + 35})`}>
            {/* RSI Border & Background */}
            <rect x={0} y={0} width={chartWidth} height={subPanelHeight} fill="#121923" rx={4} />
            <text x={10} y={16} fill="#D500F9" fontSize={10} fontWeight="bold" fontFamily="monospace">
              RSI (14): {activeCandle?.rsi ?? 52.4}
            </text>

            {/* 70 & 30 Lines */}
            <line x1={0} y1={25} x2={chartWidth} y2={25} stroke="#FF657A" strokeDasharray="3 3" opacity={0.6} />
            <text x={chartWidth + 6} y={28} fill="#FF657A" fontSize={8} fontFamily="monospace">70 OB</text>

            <line x1={0} y1={55} x2={chartWidth} y2={55} stroke="#1C2734" strokeDasharray="2 2" />
            <text x={chartWidth + 6} y={58} fill="#7E8B9B" fontSize={8} fontFamily="monospace">50</text>

            <line x1={0} y1={85} x2={chartWidth} y2={85} stroke="#38D996" strokeDasharray="3 3" opacity={0.6} />
            <text x={chartWidth + 6} y={88} fill="#38D996" fontSize={8} fontFamily="monospace">30 OS</text>

            {/* RSI Oscillator Curve */}
            <path
              d={visibleCandles.map((c, i) => {
                const x = getX(i);
                const rsiVal = c.rsi || 50;
                // Scale 0-100 into subPanel 100px - 15px
                const rsiY = 100 - (rsiVal / 100) * 80;
                return `${i === 0 ? 'M' : 'L'} ${x} ${rsiY}`;
              }).join(' ')}
              fill="none"
              stroke="#D500F9"
              strokeWidth={1.5}
            />
          </g>
        )}

        {/* Interactive Crosshair Tracking */}
        {crosshairPos && (
          <g>
            {/* Vertical crosshair line */}
            <line
              x1={crosshairPos.x}
              y1={0}
              x2={crosshairPos.x}
              y2={mainHeight + 10}
              stroke="#7E8B9B"
              strokeDasharray="2 2"
              strokeWidth={1}
            />

            {/* Horizontal crosshair line */}
            <line
              x1={0}
              y1={crosshairPos.y}
              x2={chartWidth}
              y2={crosshairPos.y}
              stroke="#7E8B9B"
              strokeDasharray="2 2"
              strokeWidth={1}
            />

            {/* Floating Price Tag at Right Axis */}
            <g transform={`translate(${chartWidth + 2}, ${crosshairPos.y - 9})`}>
              <rect x={0} y={0} width={68} height={18} fill="#1C2734" stroke="#38D996" strokeWidth={1} rx={2} />
              <text x={5} y={13} fill="#38D996" fontSize={10} fontWeight="bold" fontFamily="monospace">
                {instrument.currency}{getPriceFromY(crosshairPos.y).toFixed(instrument.market === 'forex' ? 4 : 2)}
              </text>
            </g>

            {/* Floating Time Tag at Bottom Axis */}
            {activeCandle && (
              <g transform={`translate(${crosshairPos.x - 30}, ${mainHeight + 12})`}>
                <rect x={0} y={0} width={60} height={16} fill="#1C2734" stroke="#7E8B9B" strokeWidth={0.5} rx={2} />
                <text x={30} y={12} textAnchor="middle" fill="#FFFFFF" fontSize={9} fontFamily="monospace">
                  {activeCandle.timeStr}
                </text>
              </g>
            )}
          </g>
        )}
      </svg>
    </div>
  );
};