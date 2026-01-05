#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Nov  2 12:57:46 2025


Student Name: Ice Ybanez

Student ID: R00176611

Cohort/Group/Course: SDH3-B

For task5(), the analytical task I have chosen is to question whether applying a discount changes how much a customer spends in each product category.
My findings include, for example, that when customers get a discount on Outerwear, their typical order (median) is 10.5% cheaper than orders without a discount.
"""

# Please note that you need to implement the project using functions; avoid script-based programming as it attracts negative marks.

# Do not use any library or any syntax that was not covered in the lecture or lab

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from datetime import datetime


def task1():
    df = pd.read_csv('shopping.csv')
    df = df.drop_duplicates()

    df = df[['Item Purchased', 'Shipping Type']].copy()

    df = df.dropna(subset=['Item Purchased', 'Shipping Type'])

    # make sure cols are strings
    df['Item Purchase'] = df['Item Purchased'].astype(str).str.strip().str.title()  # removing str.title() reveals more products
    df['Shipping Type'] = df['Shipping Type'].astype(str).str.strip().str.title()

    # sorted list of unique products
    products = sorted(df['Item Purchase'].unique())

    # for each product in the unique list,
    for product in products:
        # get rows for this product
        sub = df[df['Item Purchased'] == product]

        # count how many times each shipping type appears for this product
        shipping_counts = sub.groupby("Shipping Type").size()

        # total rows of shipping types for this product
        total_for_product = shipping_counts.sum()

        # sort shipping types from most to least popular
        shipping_counts = shipping_counts.sort_values(ascending=False)

        # if len(shipping_counts) == 0:
        #     print(f"No shipping types found for {product}")
        #     continue

        # building prints
        printing = []
        # for each shipping type in sorted order,
        for shipping_type in shipping_counts.index:
            # extracts the count for this shipping type for a product
            count_for_shipping = shipping_counts.loc[shipping_type]
            percentage = (count_for_shipping / total_for_product) * 100.0
            # format
            printing.append(f"{shipping_type} ({percentage:.1f}%)")

        print(product + ": " + ", ".join(printing))

# task1()

def task2():
    df = pd.read_csv('shopping.csv')
    df = df.drop_duplicates()

    # was going to do if statement, but it was too complicated but still kept amt_col
    amt_col = "Purchase Amount (USD)"

    df = df[['Gender', amt_col, 'Previous Purchases']].copy()

    df = df.dropna(subset=['Gender', amt_col, 'Previous Purchases'])

    # convert numeric columns
    df[amt_col] = pd.to_numeric(df[amt_col], errors='coerce')
    df['Previous Purchases'] = pd.to_numeric(df['Previous Purchases'], errors='coerce')
    df = df.dropna(subset=[amt_col, 'Previous Purchases'])

    # total purchased USD
    df['Total Purchased USD'] = df[amt_col] * df['Previous Purchases']

    segment_ranges = [
        ("Segment1", 0, 500),
        ("Segment2", 500, 1000),
        ("Segment3", 1000, 1500),
        ("Segment4", 1500, 2000),
        ("Segment5", 2000, 2500),
        ("Segment6", 2500, 3000),
        ("Segment7", 3000, 3500),
        ("Segment8", 3500, 4000),
        ("Segment9", 4000, 4500),
        ("Segment10", 4500, 5000),
        ("Segment11", 5000, 5500),
        ("Segment12", 5500, 6000),
    ]

    def get_segment(total_value):
        # return the segment name if total_value is in range, else None
        for segment_name, low, high in segment_ranges:
            if segment_name == "Segment12":
                if total_value >= low and total_value <= high:
                    return segment_name
            else:
                if total_value >= low and total_value < high:
                    return segment_name

        return None # if outside range 0-6000


    # keeps the name of a segment and ignores low and high values
    segments = [name for (name, _, _) in segment_ranges]

    counts = {}     # nested dict
    for seg in segments:
        counts[seg] = {'Male': 0, 'Female': 0}


    # go through each row and update counts
    for i in range(len(df)):
        gender = df.iloc[i]['Gender']   # gender for this row
        total_value = df.iloc[i]['Total Purchased USD']     # total purchased USD for this row

        # ignore NaNs and values less than 0
        if pd.isna(total_value) or total_value < 0:
            continue

        # get the segment name for this row's total purchased and update the counts for this segment and gender
        seg_name = get_segment(total_value)
        if seg_name is not None:
            counts[seg_name][gender] += 1

    # lists to hold male and female counts for each segment.
    # segment names are the indices, and the values are the counts for that segment and gender
    male_counts = []
    female_counts = []
    for seg in segments:
        male_counts.append(counts[seg]['Male'])
        female_counts.append(counts[seg]['Female'])

    # bar chart
    x_positions = list(range(len(segments)))
    width = 0.4

    plt.figure(figsize=(12, 6))

    # bars for male
    male_x = [x - width/2 for x in x_positions]     # to the left
    plt.bar(male_x, male_counts, width, label="Male")

    # bars for female
    female_x = [x + width/2 for x in x_positions]   # to the right
    plt.bar(female_x, female_counts, width, label="Female")

    plt.xticks(x_positions, segments, rotation=0)
    plt.xlabel("Segment (by Total Purchased USD)")
    plt.ylabel("Number of Customers")
    plt.title("Customer Segments by Gender")
    plt.legend()
    plt.tight_layout()
    plt.show()

    # loops over each segment and prints male and female counts for that segment from dict
    for seg in segments:
        male = counts[seg]['Male']
        female = counts[seg]['Female']
        print(f"{seg}: Male = {male}, Female = {female}")

# task2()

def task3():
    df = pd.read_csv('shopping.csv')
    df = df.drop_duplicates()

    df = df[['Item Purchased', 'Age', 'Previous Purchases']].copy()

    df = df.dropna(subset=['Item Purchased', 'Age', 'Previous Purchases'])

    # convert numeric columns
    df['Age'] = pd.to_numeric(df['Age'], errors='coerce')
    df['Previous Purchases'] = pd.to_numeric(df['Previous Purchases'], errors='coerce')
    df = df.dropna(subset=['Age', 'Previous Purchases'])

    products = sorted(df['Item Purchased'].unique())

    A_values = []
    B_values = []

    # for each product, calculate A and B, and store in lists A_values and B_values
    for product in products:
        sub = df[df['Item Purchased'] == product]

        # calculate A
        unique_ages = sorted(sub['Age'].unique())

        # for each age,
        per_age_avgs = []
        for age in unique_ages:
            # get rows where age == age
            sub_age = sub[sub['Age'] == age]

            # calculate avg of previous purchases for this age
            avg_prev_purchases = sub_age['Previous Purchases'].mean()
            # add to the list of averages for this age
            per_age_avgs.append(avg_prev_purchases)


        # A = mean of avg of previous purchases for each age
        A = sum(per_age_avgs) / len(per_age_avgs)

        # calculate B
        B = sub['Previous Purchases'].mean()

        A_values.append(A)
        B_values.append(B)


    # plot A and B
    x_positions = list(range(len(products)))
    width = 0.4

    plt.figure(figsize=(16, 5))

    # bars for A
    A_x = [x - width / 2 for x in x_positions]  # to the left
    plt.bar(A_x, A_values, width, label="A (Avg of age means)")

    # bars for B
    B_x = [x + width / 2 for x in x_positions]  # to the right
    plt.bar(B_x, B_values, width, label="B (Overall avg)")

    # x-axis labels
    plt.xticks(x_positions, products, rotation=0)
    plt.xlabel("Product")
    plt.ylabel("Average Previous Purchases")
    plt.title("A vs B by Product (Previous Purchases)")
    plt.legend()
    plt.tight_layout()
    plt.show()

# print products where A < B
    print("Products where A < B:")
    any_found = False
    # for each product,
    for i in range(len(products)):
        # if A is less than B for that product, print the product name and continue to the next product
        if A_values[i] < B_values[i]:
            print("-", products[i])
            any_found = True

    if not any_found:
        print("None")

# task3()

def task4():
    df = pd.read_csv('shopping.csv')
    df = df.drop_duplicates()

    df = df.dropna(subset=['Dates'])

    # convert 'Dates' to datetime
    df['Dates'] = pd.to_datetime(df['Dates'], errors='coerce')

    # as per spec, the first season starts at 21 March and each season lasts 3 months
    # therefore spring is March 21 - June -20,
    # summer is June 21 - September 20,
    # autumn is September 21 - December 20,
    # winter is December 21 - March 20

    # helper function to map dates to seasons
    def get_season(dt):
        month = dt.month
        day = dt.day

        if (month > 3 or (month == 3 and day >= 21)) and (month < 6 or (month == 6 and day <= 20)):
            return "Spring"
        elif (month > 6 or (month == 6 and day >= 21)) and (month < 9 or (month == 9 and day <= 20)):
            return "Summer"
        elif (month > 9 or (month == 9 and day >= 21)) and (month < 12 or (month == 12 and day <= 20)):
            return "Autumn"
        else:
            return "Winter"

    # new season column based on 'Dates'
    df['Season'] = df['Dates'].apply(get_season)

    # count rows in each season
    season_counts_series = df['Season'].value_counts()

    seasons = ["Spring", "Summer", "Autumn", "Winter"]

    # for each season,
    season_counts = []
    for season in seasons:
        # get the number of sales for this season and append to season_counts
        season_counts.append(season_counts_series.get(season, 0))   # 0 if no sales


    # month popularity

    # get month numbers from the 'Dates' column
    month_numbers = df['Dates'].dt.month

    # rows per month
    month_counts_series = month_numbers.value_counts()

    # months in order
    months = list(range(1, 13))  # 1,2,...,12
    month_counts = []
    # for each month,
    for m in months:
        # get the number of sales for this month and append to month_counts
        month_counts.append(month_counts_series.get(m, 0))


    month_names = ["January", "February", "March", "April",
                   "May", "June", "July", "August",
                   "September", "October", "November", "December"]


    plt.figure(figsize=(12, 5))

    # left subplot: season pie chart
    plt.subplot(1, 2, 1)
    plt.pie(
        season_counts,
        labels=seasons,
        autopct="%.1f%%",  # percentages 1 decimal place
        startangle=90,
        explode=[0, 0.1, 0, 0],
        shadow=True
    )
    plt.title("Popularity of Seasons (by number of sales)")
    plt.axis("equal")

    # right subplot: month bar chart
    plt.subplot(1, 2, 2)
    plt.bar(months, month_counts)
    plt.xticks(months, month_names)
    plt.xlabel("Month")
    plt.ylabel("Number of Sales")
    plt.title("Popularity of Months (by number of sales)")

    plt.tight_layout()
    plt.show()


    # print top 3 months by sales
    sorted_month_counts = month_counts_series.sort_values(ascending=False)
    top3 = list(sorted_month_counts.index)[:3]

    print("Top 3 Months based on number of sales:")
    for month in top3:
        print("-", month_names[month - 1])


def plot_yearly_sales_for_product(product_name):
    df = pd.read_csv('shopping.csv')
    df = df.drop_duplicates()
    df["Item Purchased"] = df["Item Purchased"].astype(str).str.strip().str.title()
    df['Dates'] = pd.to_datetime(df['Dates'], errors='coerce')
    df = df.dropna(subset=['Dates'])

    # clean product name
    product_clean = str(product_name).strip().title()

    # get rows for this product
    sub = df[df['Item Purchased'] == product_clean]

    # get yearly sales for this product
    years = sub['Dates'].dt.year
    year_counts_series = years.value_counts()

    # sort years in ascending order
    sorted_years = sorted(year_counts_series.index)
    year_counts = []
    for y in sorted_years:
        # get the number of sales for this year and append to year_counts
        year_counts.append(int(year_counts_series[y]))

    # plot year bar chart CHANGE TO LINE PLOT INSTEAD OF BAR CHART
    x_positions = list(range(len(sorted_years)))

    plt.figure(figsize=(8, 4))
    plt.bar(x_positions, year_counts)
    plt.xticks(x_positions, sorted_years)
    plt.xlabel("Year")
    plt.ylabel("Number of Sales")
    plt.title("Yearly Sales for " + product_clean)
    plt.tight_layout()
    plt.show()

# task4()
# use line plot instead of bar chart to see trend
# plot_yearly_sales_for_product("Shirt")

def task5():
    df = pd.read_csv('shopping.csv')
    df = df.drop_duplicates()

    # again was going to do if statement, but it was too complicated but still kept amt_col
    amt_col = "Purchase Amount (USD)"

    df = df[['Category', 'Discount Applied', amt_col]].copy()
    df = df.dropna(subset=['Category', 'Discount Applied', amt_col])

    # convert purchase amount to numeric
    df[amt_col] = pd.to_numeric(df[amt_col], errors='coerce')
    df = df.dropna(subset=[amt_col])

    # sorted list of categories
    categories = sorted(df['Category'].unique())

    # lists to store medians for Yes and No
    yes_medians = []
    no_medians = []

    # for each category, calculate the median purchase amount for Yes and No discount
    for cat in categories:
        sub_cat = df[df['Category'] == cat]

        # subset for yes and no discount
        sub_yes = sub_cat[sub_cat['Discount Applied'] == 'Yes']
        sub_no = sub_cat[sub_cat['Discount Applied'] == 'No']

        # median spend for Yes(discount applied) and median for No (no discount applied)
        yes_med = sub_yes[amt_col].median()
        no_med = sub_no[amt_col].median()

        # append to lists
        yes_medians.append(yes_med)
        no_medians.append(no_med)

    # plot grouped bar chart, one for Yes and one for No
    x_positions = list(range(len(categories)))
    width = 0.4

    plt.figure(figsize=(12, 5))

    # bars for discount, Yes (left), No (right)
    yes_x = [x - width / 2 for x in x_positions]
    plt.bar(yes_x, yes_medians, width=width, label="Discount: Yes")

    no_x = [x + width / 2 for x in x_positions]
    plt.bar(no_x, no_medians, width=width, label="Discount: No")

    plt.xticks(x_positions, categories)
    plt.xlabel("Category")
    plt.ylabel("Median Purchase Amount (USD)")
    plt.title("Median Purchase Amount by Category (Discount vs No Discount)")
    plt.legend()
    plt.tight_layout()
    plt.show()

    # short summary per category
    print("Discount effect by category:")
    # for each category, calculate the difference in median purchase amount for Yes and No discount
    for i in range(len(categories)):
        cat = categories[i]
        yes_med = yes_medians[i]
        no_med = no_medians[i]

        diff = ((yes_med - no_med) / no_med) * 100.0
        if diff >= 0:
            print(f"- {cat}: Discounted median is {diff:.1f}% HIGHER than no discount.")
        else:
            print(f"- {cat}: Discounted median is {diff:.1f}% LOWER than no discount.")

# task5()
