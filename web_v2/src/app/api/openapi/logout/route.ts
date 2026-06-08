import { NextResponse } from 'next/server';
import { NextRequest } from 'next/server';
import { getBackendOrigin } from '@/lib/config/backend';

/**
 * API: 登出
 * POST /api/openapi/logout
 *
 * 用途:
 * - 清除用户 Session
 * - 删除 Cookie
 *
 * 返回:
 * - 成功: 200 OK
 *
 * Mock模式:
 * - 设置 NEXT_PUBLIC_USE_MOCK=true 启用
 * - 通知后端清除 token（前端自行清除 localStorage）
 */
export async function POST(request: NextRequest) {
  // Mock 模式
  const useMock = process.env.NEXT_PUBLIC_USE_MOCK === 'true';

  if (useMock) {
    console.log('[Mock] POST /api/openapi/logout');

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 200));

    // Token 模式下，前端自行清除 localStorage 中的 token
    return NextResponse.json(
      {
        code: 200,
        message: '登出成功',
        data: null,
      },
      { status: 200 }
    );
  }

  // 真实后端模式
  try {
    // 构造后端 API URL
    const backendUrl = `${getBackendOrigin()}/openapi/logout`;

    // 转发 X-Auth-Token header（Token 认证模式）
    const authToken = request.headers.get('X-Auth-Token') || '';

    const response = await fetch(backendUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-BELLA-CONSOLE': 'true',
        'X-Auth-Token': authToken,
      },
    });

    const data = await response.json();

    return NextResponse.json(data, {
      status: response.status,
    });
  } catch (error) {
    console.error('[Backend POST /openapi/logout Error]', error);
    return NextResponse.json(
      {
        code: 500,
        message: '后端 API 调用失败',
        data: null,
        stacktrace: null,
        timestamp: Date.now(),
      },
      { status: 500 }
    );
  }
}
