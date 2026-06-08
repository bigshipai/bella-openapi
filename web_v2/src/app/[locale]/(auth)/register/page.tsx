"use client"

import { useEffect, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { getUserInfo } from "@/lib/api/auth"
import { LoginLayout } from "../login/components/login-layout"
import { RegisterForm } from "./components/register-form"

/**
 * 注册页面
 */
export default function RegisterPage() {
  const searchParams = useSearchParams()
  const redirect = searchParams.get('redirect') || '/overview'
  const router = useRouter()
  const [isChecking, setIsChecking] = useState(true)

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const userInfo = await getUserInfo()
        if (userInfo) {
          router.push(redirect)
          return
        }
      } catch {}
      setIsChecking(false)
    }
    checkAuth()
  }, [redirect, router])

  if (isChecking) {
    return (
      <LoginLayout>
        <div className="space-y-4 w-full max-w-sm text-center">
          <div className="animate-spin inline-block h-8 w-8 border-4 border-current border-t-transparent rounded-full" />
          <p className="text-sm text-muted-foreground">加载中...</p>
        </div>
      </LoginLayout>
    )
  }

  return (
    <LoginLayout>
      <div className="space-y-6 w-full max-w-sm">
        <div className="space-y-2 text-center">
          <h1 className="text-2xl font-bold tracking-tight">
            注册 Bella OpenAPI
          </h1>
          <p className="text-sm text-muted-foreground">
            创建账号以使用完整功能
          </p>
        </div>

        <RegisterForm redirect={redirect} />

        <p className="text-center text-sm text-muted-foreground">
          已有账号？{' '}
          <a href={`/login${redirect !== '/overview' ? `?redirect=${encodeURIComponent(redirect)}` : ''}`} className="text-primary hover:underline">
            立即登录
          </a>
        </p>
      </div>
    </LoginLayout>
  )
}
