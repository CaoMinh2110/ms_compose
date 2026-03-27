package com.kkkk.moneysaving.data.repository

import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor() : CategoryRepository {
    private val categories: List<Category> = listOf(
        Category(id = "food", name = "Food", type = CategoryType.EXPENSE, color = 0xFFFDCC1E, R.drawable.ic_cat_food),
        Category(id = "social", name = "Social", type = CategoryType.EXPENSE, color = 0xFFFDAA48, R.drawable.ic_cat_social),
        Category(id = "traffic", name = "Traffic", type = CategoryType.EXPENSE, color = 0xFF5EA5E7, R.drawable.ic_cat_traffic),
        Category(id = "shopping", name = "Shopping", type = CategoryType.EXPENSE, color = 0xFFEF7FC5, R.drawable.ic_cat_shopping),
        Category(id = "grocery", name = "Grocery", type = CategoryType.EXPENSE, color = 0xFF64D852, R.drawable.ic_cat_grocery),
        Category(id = "education", name = "Education", type = CategoryType.EXPENSE, color = 0xFFE19DDF, R.drawable.ic_cat_education),
        Category(id = "bills", name = "Bills", type = CategoryType.EXPENSE, color = 0xFF84D8BA, R.drawable.ic_cat_bills),
        Category(id = "rentals", name = "Rentals", type = CategoryType.EXPENSE, color = 0xFFF5D358, R.drawable.ic_cat_rentals),
        Category(id = "medical", name = "Medical", type = CategoryType.EXPENSE, color = 0xFFFC8484, R.drawable.ic_cat_medical),
        Category(id = "investment_expense", name = "Investment", type = CategoryType.EXPENSE, color = 0xFF6DC3C3, R.drawable.ic_cat_investment),
        Category(id = "gift", name = "Gift", type = CategoryType.EXPENSE, color = 0xFFAA8EFB, R.drawable.ic_cat_gift),
        Category(id = "other_expense", name = "Other", type = CategoryType.EXPENSE, color = 0xFF7499FC, R.drawable.ic_cat_other),

        Category(id = "salary", name = "Salary", type = CategoryType.INCOME, color = 0xFFE2BA28, R.drawable.ic_cat_salary),
        Category(id = "invest_income", name = "Invest", type = CategoryType.INCOME, color = 0xFF73C766, R.drawable.ic_cat_invest),
        Category(id = "business", name = "Business", type = CategoryType.INCOME, color = 0xFF7DBFFB, R.drawable.ic_cat_business),
        Category(id = "interest", name = "Interest", type = CategoryType.INCOME, color = 0xFFA083F6, R.drawable.ic_cat_interest),
        Category(id = "extra_income", name = "Extra income", type = CategoryType.INCOME, color = 0xFFBB6BD9, R.drawable.ic_cat_extra_income),
        Category(id = "other_income", name = "Other", type = CategoryType.INCOME, color = 0xFF7499FC, R.drawable.ic_cat_other),

        Category(id = ID_LOAN_OUT, name = "Loan", type = CategoryType.LOAN, color = 0xFF4F80FC, R.drawable.ic_cat_loan),
        Category(id = ID_BORROW_IN, name = "Borrow", type = CategoryType.LOAN, color = 0xFFAB4FFC, R.drawable.ic_cat_borrow),
    )

    override fun getAll(): List<Category> = categories

    override fun getById(id: String): Category? = categories.firstOrNull { it.id == id }

    override fun getType(id: String): CategoryType? { return getById(id)?.type }

    companion object {
        const val ID_LOAN_OUT  = "loan_out"
        const val ID_BORROW_IN = "borrow_in"
    }
}
