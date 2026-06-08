import { NextResponse } from 'next/server';
import { NextRequest } from 'next/server';
import { getBackendOrigin } from '@/lib/config/backend';

/**
 * API: 获取当前用户信息
 * GET /api/openapi/userInfo
 *
 * 用途:
 * - 应用初始化时检查登录状态
 * - 刷新用户信息
 *
 * 返回:
 * - 已登录: 返回用户信息 { userId, username, email, ... }
 * - 未登录: 返回 401
 */
export async function GET(request: NextRequest) {
  try {
    // 构造后端 API URL
    const backendUrl = `${getBackendOrigin()}/console/userInfo`;

    // 转发 X-Auth-Token header（Token 认证模式）
    const authToken = request.headers.get('X-Auth-Token') || '';

    const response = await fetch(backendUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'X-BELLA-CONSOLE': 'true',
        'X-Auth-Token': authToken,
      },
    });

    // 检查 Content-Type，确保是 JSON 响应
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      console.error('[Backend GET /console/userInfo] Non-JSON response:', {
        status: response.status,
        contentType,
      });

      // 非 JSON 响应（可能是 HTML 错误页面或重定向）
      return NextResponse.json(
        {
          code: response.status,
          message: `后端返回非 JSON 响应 (${response.status})`,
          data: null,
          stacktrace: null,
          timestamp: Date.now(),
        },
        { status: response.status }
      );
    }

    // 解析 JSON 响应
    const data = await response.json();

    return NextResponse.json(data, {
      status: response.status,
    });
  } catch (error) {
    console.error('[Backend GET /console/userInfo Error]', error);
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
