package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.models.ComplianceStatus
import com.example.data.models.PackageAngle
import com.example.data.models.ProductCategory
import com.example.data.models.RuleSeverity
import com.example.data.models.UserRole

class Converters {
    @TypeConverter
    fun fromComplianceStatus(status: ComplianceStatus?): String? = status?.name

    @TypeConverter
    fun toComplianceStatus(value: String?): ComplianceStatus? =
        value?.let { enumValueOf<ComplianceStatus>(it) }

    @TypeConverter
    fun fromRuleSeverity(severity: RuleSeverity?): String? = severity?.name

    @TypeConverter
    fun toRuleSeverity(value: String?): RuleSeverity? =
        value?.let { enumValueOf<RuleSeverity>(it) }

    @TypeConverter
    fun fromUserRole(role: UserRole?): String? = role?.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole? =
        value?.let { enumValueOf<UserRole>(it) }

    @TypeConverter
    fun fromProductCategory(category: ProductCategory?): String? = category?.name

    @TypeConverter
    fun toProductCategory(value: String?): ProductCategory? =
        value?.let { enumValueOf<ProductCategory>(it) }

    @TypeConverter
    fun fromPackageAngle(angle: PackageAngle?): String? = angle?.name

    @TypeConverter
    fun toPackageAngle(value: String?): PackageAngle? =
        value?.let { enumValueOf<PackageAngle>(it) }
}
