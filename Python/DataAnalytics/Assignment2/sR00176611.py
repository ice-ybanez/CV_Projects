#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Nov 22 17:00:02 2025


Name: Ice Ybanez
Student ID: R00176611

Cohort: SDH3-B


Task-7 (Loan Dataset)
CONCEPT and OBJECTIVE:
For Task-7, I chose to predict whether a loan will be paid back ("loan_paid_back" in Loan.csv) based on customer financial attributes and loan characteristics.
This helps banks and credit unions' analysts to assess the risk of lending money to borrowers and potentially adjust loan approval checklists/criteria or interest rates.

"""

# all implementation should be function based...


import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.decomposition import PCA
from sklearn.cluster import KMeans
from sklearn import tree
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay

from scipy.stats import pearsonr


# data loading
def load_health_data(path="Health.csv"):
    return pd.read_csv(path)

def load_loan_data(path="Loan.csv"):
    return pd.read_csv(path)


def task1(health_df):

    gender = health_df["Gender"].copy()

    # cleaning
    gender = gender.astype(str).str.strip().str.lower()

    # treat missing strings as NaN
    gender = gender.replace({
        "": np.nan,
        "nan": np.nan,
        "none": np.nan,
        "null": np.nan
    })

    # in case of typos
    gender = gender.replace({
        "m": "male", "mal": "male",
        "f": "female", "femal": "female"
    })
    # anything else to NaN
    gender = gender.where(gender.isin(["male", "female"]), np.nan)


    # majority gender
    non_missing_counts = gender.value_counts()
    if len(non_missing_counts) == 0:
        majority = "male"  # fallback (unlikely)
    else:
        majority = non_missing_counts.index[0]

    # fill missing with majority
    gender = gender.fillna(majority)

    gender = gender.replace({"male": "Male", "female": "Female"})
    health_df["Gender"] = gender

    counts = gender.value_counts()

    # printing pop of each gender separately
    print("Female:", int(counts.get("Female", 0)))
    print("Male:", int(counts.get("Male", 0)))

    # plot
    labels = ["Female", "Male"]
    values = np.array([counts.get("Female", 0), counts.get("Male", 0)], dtype=float)
    total = values.sum()

    fig, ax = plt.subplots()
    bars = ax.bar(labels, values)


    ax.set_title("Gender Distribution in Health.csv")
    ax.set_xlabel("Gender")
    ax.set_ylabel("Population Count")

    # percentage labels
    if total > 0:
        for bar, val in zip(bars, values):
            pct = (val / total) * 100
            ax.text(bar.get_x() + bar.get_width()/2, val, f"{pct:.1f}%", ha="center", va="bottom")


def task2(health_df):

    cols = ["Workout_Type", "Session_Duration (hours)", "Calories_Burned"]
    df = health_df[cols].copy()

    # cleaning
    df["Workout_Type"] = df["Workout_Type"].astype(str).str.strip().replace({"": np.nan, "nan": np.nan})
    df["Workout_Type"] = df["Workout_Type"].str.title()

    df["Session_Duration (hours)"] = pd.to_numeric(df["Session_Duration (hours)"], errors="coerce")
    df["Calories_Burned"] = pd.to_numeric(df["Calories_Burned"], errors="coerce")

    df = df.dropna(subset=cols)

    # avg cals by workout type
    mean_cal = df.groupby("Workout_Type")["Calories_Burned"].mean()

    # find the top workout type
    mean_cal_sorted = mean_cal.sort_values(ascending=False)
    top_type = mean_cal_sorted.index[0]
    top_mean = mean_cal_sorted.iloc[0]


    fig, ax = plt.subplots()

    # plot each workout type
    workout_types = df["Workout_Type"].value_counts().index
    for wt in workout_types:
        sub = df[df["Workout_Type"] == wt]
        ax.scatter(
            sub["Session_Duration (hours)"],
            sub["Calories_Burned"],
            label=wt,
            alpha=0.7,
            s=100,
            marker=".",
            linewidths=0,
            edgecolors="none"
        )


    ax.set_title("Session Duration vs Calories Burned by Workout Type")
    ax.set_xlabel("Session Duration (hours)")
    ax.set_ylabel("Calories Burned")

    ax.legend()
    ax.grid(True, alpha=0.3)
    plt.tight_layout()


    # note on chart
    ax.text(
        0.02, 0.98,
        f"Highest avg calories: {top_type} ({top_mean:.0f})",
        transform=ax.transAxes,
        ha="left", va="top"
    )
# Comment task2: The workout type "Hiit" (also on the chart) has the highest average calories burned in this dataset.


def task3(health_df):

    # #numeric-only df
    num_df = pd.DataFrame()
    for col in health_df.columns:
        if health_df[col].dtype == np.float64 or health_df[col].dtype == np.int64:
            num_df[col] = health_df[col]

    num_df = num_df.dropna()

    if len(num_df) < 3:
        return


    for class_col in num_df.columns:

        # sort by class attribute
        sorted_df = num_df.sort_values([class_col], ascending=[True])

        n = len(sorted_df)
        if n < 3:
            continue

        # 3 equal-sized buckets
        third = n // 3
        cut1 = third
        cut2 = 2 * third

        # create y
        y = ([1] * cut1) + ([2] * (cut2 - cut1)) + ([3] * (n - cut2))

        # build X
        X = pd.DataFrame()
        for col in sorted_df.columns:
            if col != class_col:
                X[col] = sorted_df[col]

        if X.shape[1] == 0:
            continue

        # DTC
        model = tree.DecisionTreeClassifier()
        model.fit(X, y)

        # feature importances as a Series
        imp = pd.Series(model.feature_importances_, index=X.columns)
        imp = imp.sort_values(ascending=False)

        # keep only importance > 0.25
        retained_025 = imp[imp > 0.25]


        if len(retained_025) >= 3:
            print(class_col)

            retained_015 = imp[imp > 0.15]
            for feat in retained_015.index:
                print(feat + ": " + "{:.3f}".format(retained_015[feat]))



def task4(loan_df):

    num_df = pd.DataFrame()
    for col in loan_df.columns:
        col_num = pd.to_numeric(loan_df[col], errors="coerce")

        # keep the column if it's not entirely NaN after conversion
        if col_num.isna().sum() != len(col_num):
            num_df[col] = col_num


    if "total_credit_limit" not in num_df.columns:
        return


    num_df = num_df.dropna()
    if len(num_df) < 3:
        return

    # discretize target into 3 equal buckets
    sorted_df = num_df.sort_values(["total_credit_limit"], ascending=[True])
    n = len(sorted_df)

    cut1 = n // 3
    cut2 = 2 * (n // 3)

    y = ([1] * cut1) + ([2] * (cut2 - cut1)) + ([3] * (n - cut2))

    # build X
    X = pd.DataFrame()
    for col in sorted_df.columns:
        if col != "total_credit_limit":
            X[col] = sorted_df[col]

    if X.shape[1] == 0:
        return

    # train-test split (30% test)
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.3, random_state=42
    )

    # DTC: experiment with max_depth
    depth_values = [1, 2, 3, 4, 5, 6, None]

    train_scores_dtc = []
    test_scores_dtc = []

    for d in depth_values:
        model = DecisionTreeClassifier(max_depth=d)
        model.fit(X_train, y_train)
        train_scores_dtc.append(model.score(X_train, y_train))
        test_scores_dtc.append(model.score(X_test, y_test))


    # plot DTC
    x_pos = list(range(len(depth_values)))
    x_labels = ["1", "2", "3", "4", "5", "6", "None"]

    plt.figure()
    plt.plot(x_pos, train_scores_dtc, marker="o", label="Train Accuracy")
    plt.plot(x_pos, test_scores_dtc, marker="o", label="Test Accuracy")
    plt.xticks(x_pos, x_labels)
    plt.title("Decision Tree Classifier (DTC): Accuracy vs max_depth")
    plt.xlabel("max_depth")
    plt.ylabel("Accuracy")
    plt.legend()

    # DTC: Overfitting is present if train accuracy becomes much higher than test accuracy as depth increases.
    # More appropriate max_depth is usually where test accuracy is high AND the gap between train and test is small.


    # KNN: experiment with n_neighbors
    k_values = [1, 3, 5, 7, 9, 11, 13]

    # scale features
    scaler = MinMaxScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)

    train_scores_knn = []
    test_scores_knn = []

    for k in k_values:
        model = KNeighborsClassifier(n_neighbors=k)
        model.fit(X_train_scaled, y_train)
        train_scores_knn.append(model.score(X_train_scaled, y_train))
        test_scores_knn.append(model.score(X_test_scaled, y_test))


    # best k based on test accuracy
    best_test_knn = max(test_scores_knn)
    best_pos_knn = test_scores_knn.index(best_test_knn)

    # plot KNN
    plt.figure()
    plt.plot(k_values, train_scores_knn, marker="o", label="Train Accuracy")
    plt.plot(k_values, test_scores_knn, marker="o", label="Test Accuracy")
    plt.title("k-Nearest Neighbours (KNN): Accuracy vs n_neighbors (k)")
    plt.xlabel("n_neighbors (k)")
    plt.ylabel("Accuracy")
    plt.legend()

    # KNN: Overfitting often appears at very small k (k = 1) where train accuracy is high but test is lower.
    # A more appropriate k is usually where test accuracy is high AND train/test accuracies are closer.
# Comment task4:
# DTC: Overfitting is present if train accuracy becomes much higher than test accuracy as depth increases.
# More appropriate max_depth is where test accuracy is high AND the gap between train and test is small.

# KNN: Overfitting often appears at very small k (k = 1) where train accuracy is high but test is lower.
# More appropriate k is where test accuracy is high AND train/test accuracies are closer.



def task5(health_df):

    exclude_cols = ["meal_name", "Benefit", "Target Muscle Group", "Gender"]

    # non-numerical cols
    cat_df = pd.DataFrame()
    for col in health_df.columns:
        if health_df[col].dtype == object and col not in exclude_cols:
            cat_df[col] = health_df[col]

    if cat_df.shape[1] == 0:
        return

    # clean missing/blank text
    for col in cat_df.columns:
        cat_df[col] = cat_df[col].astype(str).str.strip()
        cat_df[col] = cat_df[col].replace({"": "Unknown",
                                           "nan": "Unknown",
                                           "None": "Unknown",
                                           "null": "Unknown"
                                           })

    # convert categorical cols to numeric
    enc = pd.DataFrame()
    for col in cat_df.columns:
        enc[col] = cat_df[col].astype("category").cat.codes

    if len(enc) < 3:
        return

    # 2D PCA
    pca = PCA(n_components=2)
    reduced_2d = pca.fit_transform(enc.values)

    # K-Means clustering
    k = 3
    kmeans = KMeans(n_clusters=k, random_state=42)
    labels = kmeans.fit_predict(reduced_2d)

    # visualize dataset
    fig, ax = plt.subplots()
    for cluster_id in range(k):
        pts = reduced_2d[labels == cluster_id]
        ax.scatter(
            pts[:, 0], pts[:, 1],
            label="Cluster " + str(cluster_id + 1),
            alpha=0.7,
            s=100,
            marker=".",
            linewidths=0,
            edgecolors="none"
        )

    # centroids
    centers = kmeans.cluster_centers_
    ax.scatter(
        centers[:, 0], centers[:, 1],
        marker="x",
        color="red",
        s=150,
        linewidths=2,
        label="Centroids"
    )

    ax.set_title("Non-Numeric Health Features (2D PCA + KMeans)")
    ax.set_xlabel("PCA Dimension 1")
    ax.set_ylabel("PCA Dimension 2")
    ax.legend()
    plt.tight_layout()
# Comment task5: The three clusters are separated, meaning the non-numerical features form distinct categorical profiles.


def task6(health_df):

    # numeric only df
    num_df = pd.DataFrame()
    for col in health_df.columns:
        col_num = pd.to_numeric(health_df[col], errors="coerce")
        if col_num.isna().sum() != len(col_num):
            num_df[col] = col_num

    num_df = num_df.dropna()

    if num_df.shape[1] < 2 or len(num_df) < 5:
        return []

    # threshold for "strong or very strong" correlation
    threshold = 0.7

    Significant_features = []

    cols = list(num_df.columns)

    # for each numeric feature F, find strongly correlated features using pearsonr
    for F in cols:

        # skip if F is constant
        if num_df[F].nunique() < 2:
            continue

        strong_features = []

        for other in cols:
            if other != F:

                # skip if other is constant
                if num_df[other].nunique() < 2:
                    continue

                r, p_value = pearsonr(num_df[F], num_df[other])

                if abs(r) >= threshold:
                    strong_features.append(other)

        # keep F only if it has at least 4 other strongly correlated features
        if len(strong_features) >= 4:
            df_sub = num_df[[F] + strong_features].copy()
            Significant_features.append(df_sub)


    if len(Significant_features) == 0:
        return []



    # train regression for each df
    results = []

    for df_sub in Significant_features:
        all_cols = list(df_sub.columns)
        target = all_cols[0]

        # predictors
        X = df_sub[all_cols[1:]].copy()
        y = df_sub[target].copy()

        # 30% test split
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.3, random_state=42
        )

        # train regression model
        model = LinearRegression()
        model.fit(X_train, y_train)

        train_score = model.score(X_train, y_train)  # R^2
        test_score = model.score(X_test, y_test)     # R^2

        results.append([df_sub, target, len(all_cols), train_score, test_score])


    # sort by df size (small -> large) and visualize
    results.sort(key=lambda x: x[2])

    # update Significant_features list to sorted order
    Significant_features = []
    labels = []
    train_scores = []
    test_scores = []

    for item in results:
        df_sub, target, size, tr, te = item
        Significant_features.append(df_sub)
        labels.append(target + " (cols=" + str(size) + ")")
        train_scores.append(tr)
        test_scores.append(te)

    x = np.arange(len(labels))
    width = 0.35

    fig, ax = plt.subplots()
    ax.bar(x - width/2, train_scores, width, label="Train R^2")
    ax.bar(x + width/2, test_scores, width, label="Test R^2")

    ax.set_title("Regression Accuracy for Significant Feature Sets")
    ax.set_xlabel("Model (Target Feature)")
    ax.set_ylabel("R^2 Score")
    ax.set_xticks(x)
    ax.set_xticklabels(labels, rotation=45, ha="right")
    ax.legend()
    plt.tight_layout()

    return Significant_features


def task7(loan_df):

    # data cleansing and numeric conversion
    num_df = pd.DataFrame()

    for col in loan_df.columns:
        col_num = pd.to_numeric(loan_df[col], errors="coerce")
        if col_num.isna().sum() != len(col_num):
            num_df[col] = col_num

    # target col = "loan_paid_back"
    if "loan_paid_back" not in num_df.columns:
        return


    num_df = num_df.dropna()
    if len(num_df) < 10:
        return

    # define target (y) and features (X)
    y = num_df["loan_paid_back"].astype(int)

    X = pd.DataFrame()
    for col in num_df.columns:
        if col != "loan_paid_back":
            X[col] = num_df[col]


    # keep only rows where y is 0/1
    valid = y.isin([0, 1])
    X = X[valid]
    y = y[valid]

    if len(X) < 10 or X.shape[1] == 0:
        return

    # train/test split - 30% test
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.3, random_state=42
    )

    # Machine Learning model: Decision Tree Classifier
    model = DecisionTreeClassifier()
    model.fit(X_train, y_train)

    train_acc = model.score(X_train, y_train)
    test_acc = model.score(X_test, y_test)


    # confusion matrix (visual)
    y_pred = model.predict(X_test)
    cm = confusion_matrix(y_test, y_pred, labels=[0, 1])


    fig, ax = plt.subplots()
    disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=["Not Paid Back", "Paid Back"])
    disp.plot(ax=ax)


    ax.set_title("Loan Repayment Prediction (Confusion Matrix)\n"
                 + "Train Acc=" + "{:.2f}".format(train_acc)
                 + " | Test Acc=" + "{:.2f}".format(test_acc))
    plt.tight_layout()


    # feature importance (visual)
    importance = pd.Series(model.feature_importances_, index=X.columns)
    importance_sorted = importance.sort_values(ascending=True)

    fig, ax = plt.subplots(figsize=(12, 6))
    ax.bar(importance_sorted.index, importance_sorted.values)

    ax.set_title("Feature Importance (All Features - Decision Tree)")
    ax.set_xlabel("Feature")
    ax.set_ylabel("Importance")
    ax.set_xticklabels(importance_sorted.index, rotation=45, ha="right")
    plt.tight_layout()

    # create table: avg values by outcome
    compare_cols = ["debt_to_income_ratio", "credit_score", "installment", "current_balance", "interest_rate", "loan_amount"]

    # keep cols that exist in this dataset
    available = []
    for c in compare_cols:
        if c in num_df.columns:
            available.append(c)

    table = num_df[available + ["loan_paid_back"]].groupby("loan_paid_back").mean()
    table = table.rename(index={0: "Not Paid Back (0)", 1: "Paid Back (1)"})

    print(table)
# Comment task7:

# CONCEPT and OBJECTIVE:
# For Task-7, I chose to predict whether a loan will be paid back ("loan_paid_back" in Loan.csv) based on customer financial attributes and loan characteristics.
# This helps banks and credit unions' analysts to assess the risk of lending money to borrowers and potentially adjust loan approval checklists/criteria or interest rates.

# CONFUSION MATRIX
# The confusion matrix shows test performance for paid vs unpaid loans.
# from my results:
# Total: 340 + 906 + 892 + 3862 = 6000
# Predictions: 340 + 3862 = 4202
# ACCURACY: 4202/6000 = 0.70 (matches)

# The issue is when the true class is Not Paid Back, the model only correctly catches 340 and misses 906 by predicting "Paid Back"
# This is important in the case of loan since false positives like approving someone who won't repay can be costly.

# FEATURE IMPORTANCE
# The feature importance chart highlights which numeric factors most influence repayment prediction in the loan dataset.
# The longer the bar, the more the feature helped the tree make correct splits.
# The tree is mostly making decisions based on affordability and credit risk. Income matters but in this model, it seems less directly decisive than the debt-to-income ratio and credit score.

# Feature importance doesn't tell direction, like it doesn't say higher debt-to-income increases or decreases repayment.
# to get direction:

# TABLE
# Output shows debt_to_income_ratio is higher for Not Paid Back than Paid Back
# Higher dti is associated with non-repayment

# Output shows loan_amount is nearly the same for Not Paid Back and Paid Back
# Loan amount doesn't separate the groups much

# The averages show borrowers who DIDN'T pay back have a higher debt_to_income_ratio which supports the model's feature importance result:
# debt_to_income_ratio is a key factor


def main():
    health_df = load_health_data()
    loan_df = load_loan_data()

    # Please uncomment which task to run

    # task1(health_df)
    # task2(health_df)
    # task3(health_df)
    # task4(loan_df)
    # task5(health_df)
    # task6(health_df)
    # task7(loan_df)

    plt.show()


if __name__ == "__main__":
    main()


